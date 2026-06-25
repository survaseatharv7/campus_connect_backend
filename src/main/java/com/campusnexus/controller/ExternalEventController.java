package com.campusnexus.controller;

import com.campusnexus.dto.ApiResponse;
import com.campusnexus.dto.ExternalEventResponse;
import com.campusnexus.dto.ExternalRegistrationRequest;
import com.campusnexus.dto.ExternalRegistrationResponse;
import com.campusnexus.service.ExternalEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/external")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "External Events", description = "Public guest endpoints for events open to external participants")
public class ExternalEventController {

    private final ExternalEventService externalEventService;

    public ExternalEventController(ExternalEventService externalEventService) {
        this.externalEventService = externalEventService;
    }

    @GetMapping("/events")
    @Operation(summary = "Get open external events", description = "Retrieve list of all events marked open to external participants with status UPCOMING or ONGOING")
    public ResponseEntity<ApiResponse<List<ExternalEventResponse>>> getOpenExternalEvents() {
        List<ExternalEventResponse> events = externalEventService.getOpenExternalEvents();
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @PostMapping("/events/{eventId}/register")
    @Operation(summary = "Register guest for event", description = "Register an anonymous guest for an event open to external participants")
    public ResponseEntity<ApiResponse<ExternalRegistrationResponse>> registerGuest(
            @PathVariable UUID eventId,
            @Valid @RequestBody ExternalRegistrationRequest request) {
        ExternalRegistrationResponse response = externalEventService.registerGuest(eventId, request);
        return ResponseEntity.ok(ApiResponse.success("Registration processed successfully", response));
    }

    @GetMapping("/registrations")
    @Operation(summary = "Get guest registrations by email", description = "Retrieve registrations of external guests filtered by their email address")
    public ResponseEntity<ApiResponse<List<ExternalRegistrationResponse>>> getMyRegistrations(
            @RequestParam String email) {
        List<ExternalRegistrationResponse> registrations = externalEventService.getMyRegistrations(email);
        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
    }
}
