package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.exception.UnauthorizedException;
import com.campusnexus.repository.*;
import com.campusnexus.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final ClubRepository clubRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            CollegeRepository collegeRepository,
                            DepartmentRepository departmentRepository,
                            ClubRepository clubRepository,
                            EventRegistrationRepository eventRegistrationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.clubRepository = clubRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
    }

    @Override
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, UUID createdById) {
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
                .ticketPrice(request.getTicketPrice())
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
        List<Event> events = eventRepository.findByCollegeIdOrEventLevelOrderByStartDateTimeDesc(collegeId, EventLevel.CAMPUS);
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
        event.setStatus(EventStatus.UPCOMING);
        event = eventRepository.save(event);
        return mapToResponse(event, null);
    }

    @Override
    @Transactional
    public EventResponse updateEventStatus(UUID eventId, EventStatus status, UUID currentUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getCreatedBy().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only event creator can update status");
        }

        event.setStatus(status);
        event = eventRepository.save(event);
        return mapToResponse(event, null);
    }

    private void checkAndUpdateStatus(Event event) {
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            return;
        }
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        boolean changed = false;
        if (now.isAfter(event.getEndDateTime())) {
            event.setStatus(EventStatus.COMPLETED);
            changed = true;
        } else if (now.isAfter(event.getStartDateTime()) && now.isBefore(event.getEndDateTime()) && event.getStatus() != EventStatus.ONGOING) {
            event.setStatus(EventStatus.ONGOING);
            changed = true;
        }
        if (changed) {
            eventRepository.save(event);
        }
    }

    private EventResponse mapToResponse(Event event, Set<UUID> registeredEventIds) {
        checkAndUpdateStatus(event);
        List<EventResponse> subEvents = eventRepository.findByParentEventIdOrderByStartDateTimeDesc(event.getId())
                .stream()
                .map(sub -> {
                    checkAndUpdateStatus(sub);
                    return EventResponse.builder()
                            .id(sub.getId())
                            .title(sub.getTitle())
                            .description(sub.getDescription())
                            .eventLevel(sub.getEventLevel())
                            .eventType(sub.getEventType())
                            .status(sub.getStatus())
                            .startDateTime(sub.getStartDateTime())
                            .endDateTime(sub.getEndDateTime())
                            .venue(sub.getVenue())
                            .createdByName(sub.getCreatedBy().getName())
                            .createdAt(sub.getCreatedAt())
                            .registeredCount(sub.getRegisteredCount())
                            .participantCount(sub.getRegisteredCount())
                            .isRegistered(registeredEventIds != null && registeredEventIds.contains(sub.getId()))
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
                .status(event.getStatus())
                .createdByName(event.getCreatedBy().getName())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .venue(event.getVenue())
                .maxParticipants(event.getMaxParticipants())
                .registeredCount(event.getRegisteredCount())
                .participantCount(event.getRegisteredCount())
                .isPaid(event.getIsPaid())
                .ticketPrice(event.getTicketPrice())
                .createdAt(event.getCreatedAt())
                .isRegistered(registeredEventIds != null && registeredEventIds.contains(event.getId()))
                .subEvents(subEvents.isEmpty() ? null : subEvents)
                .build();
    }
}
