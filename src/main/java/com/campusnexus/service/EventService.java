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
}
