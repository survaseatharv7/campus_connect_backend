package com.campusnexus.controller;

import com.campusnexus.dto.ApiResponse;
import com.campusnexus.dto.EventParticipantResponse;
import com.campusnexus.dto.EventResponse;
import com.campusnexus.dto.UpdateEventStatusRequest;
import com.campusnexus.entity.User;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.EventRegistrationService;
import com.campusnexus.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Global Events", description = "Global Event endpoints accessible conditionally based on roles")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EventController {

    private final EventRegistrationService eventRegistrationService;
    private final EventService eventService;
    private final UserRepository userRepository;

    public EventController(EventRegistrationService eventRegistrationService,
                           EventService eventService,
                           UserRepository userRepository) {
        this.eventRegistrationService = eventRegistrationService;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{eventId}/participants")
    @Operation(summary = "List event participants", description = "Get list of registered participants (requires admin, principal, hod, professor or being the creator)")
    public ResponseEntity<ApiResponse<List<EventParticipantResponse>>> getParticipants(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Participants retrieved",
                eventRegistrationService.getParticipants(eventId, user.getId())));
    }

    @PutMapping("/{eventId}/status")
    @Operation(summary = "Update event status", description = "Update status of an event (requires creator privileges)")
    public ResponseEntity<ApiResponse<EventResponse>> updateEventStatus(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Event status updated",
                eventService.updateEventStatus(eventId, request.getStatus(), user.getId())));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
