package com.campusnexus.service.impl;

import com.campusnexus.dto.ExternalEventResponse;
import com.campusnexus.dto.ExternalRegistrationRequest;
import com.campusnexus.dto.ExternalRegistrationResponse;
import com.campusnexus.entity.Event;
import com.campusnexus.entity.ExternalRegistration;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.enums.PaymentStatus;
import com.campusnexus.enums.TicketStatus;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.DuplicateResourceException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.EventRepository;
import com.campusnexus.repository.ExternalRegistrationRepository;
import com.campusnexus.service.ExternalEventService;
import com.campusnexus.util.EventStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExternalEventServiceImpl implements ExternalEventService {

    private final EventRepository eventRepository;
    private final ExternalRegistrationRepository externalRegistrationRepository;
    private final StripeService stripeService;

    public ExternalEventServiceImpl(EventRepository eventRepository,
                                   ExternalRegistrationRepository externalRegistrationRepository,
                                   StripeService stripeService) {
        this.eventRepository = eventRepository;
        this.externalRegistrationRepository = externalRegistrationRepository;
        this.stripeService = stripeService;
    }

    @Override
    public List<ExternalEventResponse> getOpenExternalEvents() {
        List<EventStatus> activeStatuses = Arrays.asList(EventStatus.UPCOMING, EventStatus.ONGOING);
        return eventRepository.findByOpenToExternalTrueAndStatusInOrderByStartDateTimeDesc(activeStatuses)
                .stream()
                .map(this::mapToExternalEventResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExternalRegistrationResponse registerGuest(UUID eventId, ExternalRegistrationRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.isOpenToExternal()) {
            throw new BadRequestException("This event is not open to external guests");
        }

        // ── Issue #3 fix: validate event status before accepting external registration ─
        EventStatus effectiveStatus = EventStatusUtil.compute(event);
        if (effectiveStatus == EventStatus.COMPLETED) {
            throw new BadRequestException("External registration is closed — this event has already ended.");
        }
        if (effectiveStatus == EventStatus.CANCELLED) {
            throw new BadRequestException("External registration is closed — this event has been cancelled.");
        }
        // ────────────────────────────────────────────────────────────────────────────

        if (externalRegistrationRepository.existsByEvent_IdAndGuestEmail(eventId, request.getGuestEmail())) {
            throw new DuplicateResourceException("Already registered with this email");
        }

        if (event.getMaxParticipants() != null && event.getRegisteredCount() >= event.getMaxParticipants()) {
            throw new BadRequestException("Event is full");
        }

        ExternalRegistration registration = ExternalRegistration.builder()
                .event(event)
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .collegeName(request.getCollegeName())
                .city(request.getCity())
                .build();

        if (Boolean.TRUE.equals(event.getIsPaid()) && event.getTicketPrice() != null && event.getTicketPrice().doubleValue() > 0) {
            Map<String, String> paymentResult = stripeService.createPaymentIntent(
                    event.getTicketPrice(), "inr", eventId, null);

            registration.setTicketStatus(TicketStatus.PENDING);
            registration.setPaymentStatus(PaymentStatus.PENDING);
            registration.setStripePaymentIntentId(paymentResult.get("paymentIntentId"));
            registration.setStripeClientSecret(paymentResult.get("clientSecret"));
        } else {
            registration.setTicketStatus(TicketStatus.CONFIRMED);
            registration.setPaymentStatus(PaymentStatus.SUCCESS);

            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventRepository.save(event);
        }

        registration = externalRegistrationRepository.save(registration);
        return mapToResponse(registration);
    }

    @Override
    public List<ExternalRegistrationResponse> getMyRegistrations(String guestEmail) {
        return externalRegistrationRepository.findByGuestEmail(guestEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExternalRegistrationResponse> getRegistrationsByEvent(UUID eventId) {
        return externalRegistrationRepository.findByEvent_Id(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExternalEventResponse mapToExternalEventResponse(Event event) {
        return ExternalEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .venue(event.getVenue())
                .eventLevel(event.getEventLevel())
                .ticketPrice(event.getTicketPrice())
                .maxParticipants(event.getMaxParticipants())
                .posterUrl(event.getPosterUrl())
                .status(event.getStatus())
                .build();
    }

    private ExternalRegistrationResponse mapToResponse(ExternalRegistration reg) {
        return ExternalRegistrationResponse.builder()
                .id(reg.getId())
                .eventId(reg.getEvent().getId())
                .eventTitle(reg.getEvent().getTitle())
                .guestName(reg.getGuestName())
                .guestEmail(reg.getGuestEmail())
                .collegeName(reg.getCollegeName())
                .city(reg.getCity())
                .ticketStatus(reg.getTicketStatus())
                .paymentStatus(reg.getPaymentStatus())
                .stripeClientSecret(reg.getStripeClientSecret())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }
}
