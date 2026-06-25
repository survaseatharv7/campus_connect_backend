package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;
import com.campusnexus.enums.EventStatus;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request, UUID createdById);

    List<EventResponse> getEventsByLevel(String level);

    List<EventResponse> getEventsByCollege(UUID collegeId);

    List<EventResponse> getEventsByDepartment(UUID departmentId);

    List<EventResponse> getEventsByCreator(UUID createdById);

    List<EventResponse> getVisibleEventsForStudent(UUID collegeId, UUID departmentId, UUID studentId);

    EventResponse approveEvent(UUID eventId);

    EventResponse updateEventStatus(UUID eventId, EventStatus status, UUID currentUserId);

    /**
     * Permanently delete an event and all its dependent data.
     * Only the original event creator (owner) may call this.
     *
     * <p>Cascade order:
     * <ol>
     *   <li>ExternalRegistration records for sub-events</li>
     *   <li>EventRegistration records for sub-events</li>
     *   <li>Sub-events themselves</li>
     *   <li>ExternalRegistration records for the parent event</li>
     *   <li>EventRegistration records for the parent event</li>
     *   <li>Firebase poster image (best-effort)</li>
     *   <li>The event itself</li>
     * </ol>
     *
     * @param eventId       UUID of the event to delete
     * @param currentUserId UUID of the authenticated user requesting deletion
     * @throws com.campusnexus.exception.ResourceNotFoundException if event not found
     * @throws com.campusnexus.exception.UnauthorizedException     if caller is not the owner
     */
    void deleteEvent(UUID eventId, UUID currentUserId);
}

