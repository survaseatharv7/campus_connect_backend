package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.exception.UnauthorizedException;
import com.campusnexus.repository.*;
import com.campusnexus.service.EventService;
import com.campusnexus.util.EventStatusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final ClubRepository clubRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ExternalRegistrationRepository externalRegistrationRepository;
    private final FirebaseStorageService firebaseStorageService;

    public EventServiceImpl(EventRepository eventRepository,
            UserRepository userRepository,
            CollegeRepository collegeRepository,
            DepartmentRepository departmentRepository,
            ClubRepository clubRepository,
            EventRegistrationRepository eventRegistrationRepository,
            ExternalRegistrationRepository externalRegistrationRepository,
            FirebaseStorageService firebaseStorageService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.clubRepository = clubRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.externalRegistrationRepository = externalRegistrationRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Override
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, UUID createdById) {
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Normalize ticket price — negative values are not permitted
        BigDecimal ticketPrice = request.getTicketPrice();
        if (ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            ticketPrice = BigDecimal.ZERO;
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .posterUrl(request.getPosterUrl())
                .eventLevel(request.getEventLevel())
                .eventType(request.getEventType())
                .status(EventStatus.UPCOMING)
                .createdBy(creator)
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .venue(request.getVenue())
                .maxParticipants(request.getMaxParticipants())
                .isPaid(request.getIsPaid() != null ? request.getIsPaid() : false)
                .ticketPrice(ticketPrice)
                .openToExternal(request.isOpenToExternal())
                .build();

        if (request.getEventLevel() == EventLevel.COLLEGE) {
            if (creator.getCollege() == null) {
                throw new IllegalStateException("Creator is not assigned to any college");
            }
            event.setCollege(creator.getCollege());
        } else if (request.getEventLevel() == EventLevel.DEPARTMENT) {
            if (creator.getDepartment() == null) {
                throw new IllegalStateException("Creator is not assigned to any department");
            }
            event.setDepartment(creator.getDepartment());
            event.setCollege(creator.getCollege());
        }

        if (request.getCollegeId() != null) {
            event.setCollege(collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found")));
        }
        if (request.getDepartmentId() != null) {
            event.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }
        if (request.getClubId() != null) {
            event.setClub(clubRepository.findById(request.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found")));
        }
        if (request.getParentEventId() != null) {
            event.setParentEvent(eventRepository.findById(request.getParentEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent event not found")));
        }

        if (event.getEventLevel() == EventLevel.COLLEGE && event.getCollege() == null) {
            throw new IllegalArgumentException("College must be set for COLLEGE level event");
        }
        if (event.getEventLevel() == EventLevel.DEPARTMENT && event.getDepartment() == null) {
            throw new IllegalArgumentException("Department must be set for DEPARTMENT level event");
        }

        event = eventRepository.save(event);
        return mapToResponse(event, null);
    }

    @Override
    public List<EventResponse> getEventsByLevel(String level) {
        EventLevel eventLevel = EventLevel.valueOf(level.toUpperCase());
        return eventRepository.findByEventLevelOrderByStartDateTimeDesc(eventLevel).stream()
                .map(event -> mapToResponse(event, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getEventsByCollege(UUID collegeId) {
        return eventRepository.findByCollegeIdOrderByStartDateTimeDesc(collegeId).stream()
                .map(event -> mapToResponse(event, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getEventsByDepartment(UUID departmentId) {
        return eventRepository.findByDepartmentIdOrderByStartDateTimeDesc(departmentId).stream()
                .map(event -> mapToResponse(event, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getEventsByCreator(UUID createdById) {
        return eventRepository.findByCreatedByIdOrderByStartDateTimeDesc(createdById).stream()
                .map(event -> mapToResponse(event, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getVisibleEventsForStudent(UUID collegeId, UUID departmentId, UUID studentId) {
        List<Event> events = eventRepository.findByCollegeIdOrEventLevelOrderByStartDateTimeDesc(collegeId,
                EventLevel.CAMPUS);
        List<Event> deptEvents = eventRepository.findByDepartmentIdOrderByStartDateTimeDesc(departmentId);
        events.addAll(deptEvents);

        Set<UUID> registeredEventIds = eventRegistrationRepository.findByStudentId(studentId)
                .stream()
                .map(reg -> reg.getEvent().getId())
                .collect(Collectors.toSet());

        return events.stream()
                .distinct()
                .sorted((e1, e2) -> e2.getStartDateTime().compareTo(e1.getStartDateTime()))
                .map(event -> mapToResponse(event, registeredEventIds))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponse approveEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        // Approved events start as UPCOMING — the DB column stores UPCOMING as the base.
        // The dynamic compute will determine the actual displayed status.
        event.setStatus(EventStatus.UPCOMING);
        event = eventRepository.save(event);
        return mapToResponse(event, null);
    }

    /**
     * Update the persisted status of an event.
     *
     * <p>Only {@link EventStatus#CANCELLED} may be set manually. Time-based statuses
     * (UPCOMING, ONGOING, COMPLETED) are computed dynamically in {@link #mapToResponse}
     * and must NOT be written to the database through this method.
     *
     * @throws BadRequestException   if an attempt is made to manually set a time-based status
     * @throws UnauthorizedException if the caller is not the event creator
     */
    @Override
    @Transactional
    public EventResponse updateEventStatus(UUID eventId, EventStatus status, UUID currentUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getCreatedBy().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only event creator can update status");
        }

        // Guard: only CANCELLED is a valid manual override.
        if (status != EventStatus.CANCELLED) {
            throw new BadRequestException(
                    "Only CANCELLED status can be set manually. " +
                    "UPCOMING, ONGOING, and COMPLETED are computed automatically from event dates.");
        }

        event.setStatus(EventStatus.CANCELLED);
        event = eventRepository.save(event);
        return mapToResponse(event, null);
    }

    /**
     * Permanently delete an event and all its dependent data.
     *
     * <p>Cascade order (explicit, to respect FK constraints):
     * <ol>
     *   <li>External registrations for each sub-event</li>
     *   <li>Internal registrations for each sub-event</li>
     *   <li>Sub-events (children of this event)</li>
     *   <li>External registrations for the parent event</li>
     *   <li>Internal registrations for the parent event</li>
     *   <li>Firebase poster image — best-effort (failure is logged, not re-thrown)</li>
     *   <li>The event entity itself</li>
     * </ol>
     */
    @Override
    @Transactional
    public void deleteEvent(UUID eventId, UUID currentUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getCreatedBy().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only the event owner can delete this event");
        }

        // 1. Handle sub-events first to avoid FK violations
        List<Event> subEvents = eventRepository.findByParentEventIdOrderByStartDateTimeDesc(eventId);
        for (Event sub : subEvents) {
            deleteDependentRecords(sub.getId());
            eventRepository.delete(sub);
        }

        // 2. Delete this event's own dependent records
        deleteDependentRecords(eventId);

        // 3. Best-effort Firebase image cleanup
        String posterUrl = event.getPosterUrl();
        if (posterUrl != null && !posterUrl.isBlank()) {
            try {
                firebaseStorageService.deleteFile(posterUrl);
            } catch (Exception ex) {
                logger.warn("Failed to delete Firebase image for event {}: {}", eventId, ex.getMessage());
                // Do not abort event deletion because of image cleanup failure
            }
        }

        // 4. Delete the event itself
        eventRepository.delete(event);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────────

    /**
     * Delete all registration records (internal + external) for a given event ID.
     * Called before deleting the event row itself.
     */
    private void deleteDependentRecords(UUID eventId) {
        List<EventRegistration> internalRegs = eventRegistrationRepository.findByEventId(eventId);
        if (!internalRegs.isEmpty()) {
            eventRegistrationRepository.deleteAll(internalRegs);
        }

        List<ExternalRegistration> externalRegs = externalRegistrationRepository.findByEvent_Id(eventId);
        if (!externalRegs.isEmpty()) {
            externalRegistrationRepository.deleteAll(externalRegs);
        }
    }

    /**
     * Map an {@link Event} entity to an {@link EventResponse} DTO.
     *
     * <p><b>Status rule:</b> The status field in the DTO is ALWAYS computed
     * dynamically via {@link EventStatusUtil#compute(Event)} and is NEVER taken
     * directly from the database. This ensures time-based transitions (UPCOMING →
     * ONGOING → COMPLETED) happen without any database writes during reads.
     *
     * <p><b>Price rule:</b> {@code ticketPrice} is normalised to {@code 0} if the
     * stored value is negative (legacy data guard).
     */
    private EventResponse mapToResponse(Event event, Set<UUID> registeredEventIds) {
        // Compute effective status without writing to DB
        EventStatus effectiveStatus = EventStatusUtil.compute(event);

        // Normalise negative prices (legacy data guard — should not occur after migration)
        BigDecimal ticketPrice = event.getTicketPrice();
        if (ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            ticketPrice = BigDecimal.ZERO;
        }

        // Build sub-event list
        List<EventResponse> subEvents = eventRepository.findByParentEventIdOrderByStartDateTimeDesc(event.getId())
                .stream()
                .map(sub -> {
                    EventStatus subStatus = EventStatusUtil.compute(sub);
                    BigDecimal subPrice = sub.getTicketPrice();
                    if (subPrice != null && subPrice.compareTo(BigDecimal.ZERO) < 0) {
                        subPrice = BigDecimal.ZERO;
                    }
                    return EventResponse.builder()
                            .id(sub.getId())
                            .title(sub.getTitle())
                            .description(sub.getDescription())
                            .eventLevel(sub.getEventLevel())
                            .eventType(sub.getEventType())
                            .status(subStatus)
                            .startDateTime(sub.getStartDateTime())
                            .endDateTime(sub.getEndDateTime())
                            .venue(sub.getVenue())
                            .createdByName(sub.getCreatedBy().getName())
                            .createdAt(sub.getCreatedAt())
                            .registeredCount(sub.getRegisteredCount())
                            .participantCount(sub.getRegisteredCount())
                            .isPaid(sub.getIsPaid())
                            .ticketPrice(subPrice)
                            .isRegistered(registeredEventIds != null && registeredEventIds.contains(sub.getId()))
                            .openToExternal(sub.isOpenToExternal())
                            .build();
                })
                .collect(Collectors.toList());

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .posterUrl(event.getPosterUrl())
                .eventLevel(event.getEventLevel())
                .eventType(event.getEventType())
                .status(effectiveStatus)
                .createdByName(event.getCreatedBy().getName())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .venue(event.getVenue())
                .maxParticipants(event.getMaxParticipants())
                .registeredCount(event.getRegisteredCount())
                .participantCount(event.getRegisteredCount())
                .isPaid(event.getIsPaid())
                .ticketPrice(ticketPrice)
                .createdAt(event.getCreatedAt())
                .isRegistered(registeredEventIds != null && registeredEventIds.contains(event.getId()))
                .openToExternal(event.isOpenToExternal())
                .subEvents(subEvents.isEmpty() ? null : subEvents)
                .build();
    }
}
