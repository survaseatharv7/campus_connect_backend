package com.campusnexus.service;

import com.campusnexus.dto.ExternalEventResponse;
import com.campusnexus.dto.ExternalRegistrationRequest;
import com.campusnexus.dto.ExternalRegistrationResponse;

import java.util.List;
import java.util.UUID;

public interface ExternalEventService {
    List<ExternalEventResponse> getOpenExternalEvents();
    ExternalRegistrationResponse registerGuest(UUID eventId, ExternalRegistrationRequest request);
    List<ExternalRegistrationResponse> getMyRegistrations(String guestEmail);
    List<ExternalRegistrationResponse> getRegistrationsByEvent(UUID eventId);
}
