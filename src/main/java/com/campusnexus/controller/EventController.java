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
    @Operation(
        summary = "List event participants",
        description = "Get list of registered participants (requires admin, principal, hod, professor or being the creator)"
    )
    public ResponseEntity<ApiResponse<List<EventParticipantResponse>>> getParticipants(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Participants retrieved",
                eventRegistrationService.getParticipants(eventId, user.getId())));
    }

    /**
     * Update the status of an event.
     *
     * <p><b>Important:</b> Only {@code CANCELLED} is a valid manual status override.
     * Time-based statuses (UPCOMING, ONGOING, COMPLETED) are computed dynamically and
     * cannot be set via this endpoint. Attempting to set them returns {@code 400 Bad Request}.
     */
    @PutMapping("/{eventId}/status")
    @Operation(
        summary = "Cancel an event",
        description = "Set an event status to CANCELLED. Only the event creator may call this. " +
                      "UPCOMING, ONGOING, and COMPLETED are derived automatically from event dates " +
                      "and cannot be set manually."
    )
    public ResponseEntity<ApiResponse<EventResponse>> updateEventStatus(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Event status updated",
                eventService.updateEventStatus(eventId, request.getStatus(), user.getId())));
    }

    /**
     * Permanently delete an event.
     *
     * <p>Only the event creator (owner) may delete their own event. The deletion
     * cascades to all dependent records: internal registrations, external registrations,
     * and sub-events. The Firebase poster image is also removed on a best-effort basis.
     *
     * <p>Returns {@code 200 OK} with a success message on completion.
     */
    @DeleteMapping("/{eventId}")
    @Operation(
        summary = "Delete an event (owner only)",
        description = "Permanently deletes an event and all its associated registrations, " +
                      "sub-events, and Firebase poster image. Only the original event creator may call this."
    )
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        eventService.deleteEvent(eventId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully"));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
