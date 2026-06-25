package com.campusnexus.service.impl;

import com.campusnexus.dto.EventParticipantResponse;
import com.campusnexus.dto.EventRegistrationResponse;
import com.campusnexus.entity.Event;
import com.campusnexus.entity.EventRegistration;
import com.campusnexus.entity.User;
import com.campusnexus.enums.PaymentStatus;
import com.campusnexus.enums.TicketStatus;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.DuplicateResourceException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.exception.UnauthorizedException;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.repository.EventRegistrationRepository;
import com.campusnexus.repository.EventRepository;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.EventRegistrationService;
import com.campusnexus.service.NotificationService;
import com.campusnexus.util.EventStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;
    private final NotificationService notificationService;

    public EventRegistrationServiceImpl(EventRegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            StripeService stripeService,
            NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.stripeService = stripeService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public EventRegistrationResponse registerForEvent(UUID eventId, UUID studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // ── Issue #3 fix: validate event status before accepting registration ──────
        EventStatus effectiveStatus = EventStatusUtil.compute(event);
        if (effectiveStatus == EventStatus.COMPLETED) {
            throw new BadRequestException("Registration is closed — this event has already ended.");
        }
        if (effectiveStatus == EventStatus.CANCELLED) {
            throw new BadRequestException("Registration is closed — this event has been cancelled.");
        }
        // ─────────────────────────────────────────────────────────────────────────

        if (registrationRepository.existsByEventIdAndStudentId(eventId, studentId)) {
            throw new DuplicateResourceException("Already registered for this event");
        }

        if (event.getMaxParticipants() != null && event.getRegisteredCount() >= event.getMaxParticipants()) {
            throw new BadRequestException("Event is full");
        }

        String ticketCode = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .student(student)
                .ticketCode(ticketCode)
                .build();

        String stripeClientSecret = null;

        if (Boolean.TRUE.equals(event.getIsPaid()) && event.getTicketPrice() != null) {
            // Paid event — create Stripe PaymentIntent
            Map<String, String> paymentResult = stripeService.createPaymentIntent(
                    event.getTicketPrice(), "inr", eventId, studentId);

            registration.setTicketStatus(TicketStatus.PENDING);
            registration.setPaymentStatus(PaymentStatus.PENDING);
            registration.setStripePaymentIntentId(paymentResult.get("paymentIntentId"));
            stripeClientSecret = paymentResult.get("clientSecret");
        } else {
            // Free event — auto-confirm
            registration.setTicketStatus(TicketStatus.CONFIRMED);
            registration.setPaymentStatus(PaymentStatus.SUCCESS);

            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventRepository.save(event);

            notificationService.sendEmail(
                    student.getEmail(),
                    "Event Registration Confirmed - " + event.getTitle(),
                    "You have been registered for " + event.getTitle() +
                            ".\nTicket Code: " + ticketCode +
                            "\nVenue: " + event.getVenue() +
                            "\nDate: " + event.getStartDateTime());
        }

        registration = registrationRepository.save(registration);

        return EventRegistrationResponse.builder()
                .id(registration.getId())
                .eventId(event.getId())
                .ticketCode(registration.getTicketCode())
                .ticketStatus(registration.getTicketStatus())
                .paymentStatus(registration.getPaymentStatus())
                .stripeClientSecret(stripeClientSecret)
                .eventTitle(event.getTitle())
                .eventDate(event.getStartDateTime())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }

    @Override
    public List<EventRegistrationResponse> getStudentRegistrations(UUID studentId) {
        return registrationRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventRegistrationResponse getTicketDetails(UUID eventId, UUID studentId) {
        EventRegistration registration = registrationRepository
                .findByEventIdAndStudentId(eventId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        return mapToResponse(registration);
    }

    @Override
    public List<EventParticipantResponse> getParticipants(UUID eventId, UUID currentUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isCreator = event.getCreatedBy().getId().equals(currentUserId);
        boolean isAuthorizedRole = currentUser.getRole().name().equals("CAMPUS_ADMIN") ||
                currentUser.getRole().name().equals("PRINCIPAL") ||
                currentUser.getRole().name().equals("HOD") ||
                currentUser.getRole().name().equals("PROFESSOR");

        if (!isCreator && !isAuthorizedRole) {
            throw new UnauthorizedException("Only event creator or authorized roles can view participants");
        }

        return registrationRepository.findByEventId(eventId).stream()
                .map(this::mapToParticipantResponse)
                .collect(Collectors.toList());
    }

    private EventParticipantResponse mapToParticipantResponse(EventRegistration reg) {
        return EventParticipantResponse.builder()
                .registrationId(reg.getId())
                .studentId(reg.getStudent().getId())
                .studentName(reg.getStudent().getName())
                .studentEmail(reg.getStudent().getEmail())
                .ticketCode(reg.getTicketCode())
                .ticketStatus(reg.getTicketStatus())
                .paymentStatus(reg.getPaymentStatus())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }

    private EventRegistrationResponse mapToResponse(EventRegistration reg) {
        return EventRegistrationResponse.builder()
                .id(reg.getId())
                .eventId(reg.getEvent().getId())
                .ticketCode(reg.getTicketCode())
                .ticketStatus(reg.getTicketStatus())
                .paymentStatus(reg.getPaymentStatus())
                .eventTitle(reg.getEvent().getTitle())
                .eventDate(reg.getEvent().getStartDateTime())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }
}
