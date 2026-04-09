package com.campusnexus.service;

import com.campusnexus.dto.EventParticipantResponse;
import com.campusnexus.dto.EventRegistrationResponse;

import java.util.List;
import java.util.UUID;

public interface EventRegistrationService {
    EventRegistrationResponse registerForEvent(UUID eventId, UUID studentId);
    List<EventRegistrationResponse> getStudentRegistrations(UUID studentId);
    EventRegistrationResponse getTicketDetails(UUID eventId, UUID studentId);
    List<EventParticipantResponse> getParticipants(UUID eventId, UUID currentUserId);
}
