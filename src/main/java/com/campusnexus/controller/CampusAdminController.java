package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.User;
import com.campusnexus.enums.CollegeStatus;
import com.campusnexus.enums.SeminarHallType;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('CAMPUS_ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Campus Admin", description = "Campus Admin management endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CampusAdminController {

    private final CollegeService collegeService;
    private final SeminarHallService seminarHallService;
    private final EventService eventService;
    private final BroadcastService broadcastService;
    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final EventRegistrationService eventRegistrationService;

    public CampusAdminController(CollegeService collegeService,
                                 SeminarHallService seminarHallService,
                                 EventService eventService,
                                 BroadcastService broadcastService,
                                 DashboardService dashboardService,
                                 UserRepository userRepository,
                                 EventRegistrationService eventRegistrationService) {
        this.collegeService = collegeService;
        this.seminarHallService = seminarHallService;
        this.eventService = eventService;
        this.broadcastService = broadcastService;
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
        this.eventRegistrationService = eventRegistrationService;
    }

    // ─── User Search ─────────────────────────────────────────────────────────────

    @GetMapping("/search-users")
    @Operation(summary = "Search users", description = "Search users by name, email, or phone for role assignment")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUsers(
            @RequestParam String query) {
        List<UserSearchResponse> results = userRepository.searchByNameEmailOrPhone(query)
                .stream()
                .map(u -> UserSearchResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole() != null ? u.getRole().name() : null)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<UserSearchResponse>>success("Users found", results));
    }

    // ─── Colleges ────────────────────────────────────────────────────────────────

    @PostMapping("/colleges")
    @Operation(summary = "Create college", description = "Create a new college with unique code")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "College created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<CollegeResponse>> createCollege(
            @Valid @RequestBody CollegeCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        CollegeResponse response = collegeService.createCollege(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("College created successfully", response));
    }

    @GetMapping("/colleges")
    @Operation(summary = "List all colleges", description = "Get all colleges")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Colleges retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllColleges() {
        List<CollegeResponse> response = collegeService.getAllColleges();
        return ResponseEntity.ok(ApiResponse.success("Colleges retrieved", response));
    }

    @PutMapping("/colleges/{id}/approve")
    @Operation(summary = "Approve college", description = "Approve a pending college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "College approved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "College not found")
    })
    public ResponseEntity<ApiResponse<CollegeResponse>> approveCollege(@PathVariable UUID id) {
        CollegeResponse response = collegeService.approveCollege(id);
        return ResponseEntity.ok(ApiResponse.success("College approved", response));
    }

    @PutMapping("/colleges/{id}/status")
    @Operation(summary = "Update college status", description = "Activate or deactivate a college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "College not found")
    })
    public ResponseEntity<ApiResponse<CollegeResponse>> updateCollegeStatus(
            @PathVariable UUID id, @RequestParam CollegeStatus status) {
        CollegeResponse response = collegeService.updateCollegeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("College status updated", response));
    }

    @PutMapping("/colleges/{id}/assign-principal")
    @Operation(summary = "Assign principal", description = "Assign a principal to a college by user email")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Principal assigned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<ApiResponse<CollegeResponse>> assignPrincipal(
            @PathVariable UUID id, @Valid @RequestBody AssignPrincipalRequest request) {
        CollegeResponse response = collegeService.assignPrincipal(id, request);
        return ResponseEntity.ok(ApiResponse.success("Principal assigned", response));
    }

    // ─── Seminar Halls ───────────────────────────────────────────────────────────

    @PostMapping("/seminar-halls")
    @Operation(summary = "Create public seminar hall", description = "Create a public seminar hall")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hall created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<SeminarHallResponse>> createSeminarHall(
            @Valid @RequestBody SeminarHallRequest request) {
        // Admin creates PUBLIC halls — no college association needed
        request.setHallType(SeminarHallType.PUBLIC);
        request.setCollegeId(null);
        SeminarHallResponse response = seminarHallService.createSeminarHall(request);
        return ResponseEntity.ok(ApiResponse.success("Seminar hall created", response));
    }

    @GetMapping("/seminar-halls")
    @Operation(summary = "List public seminar halls", description = "Get all public seminar halls")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Halls retrieved")
    })
    public ResponseEntity<ApiResponse<List<SeminarHallResponse>>> getPublicSeminarHalls() {
        List<SeminarHallResponse> response = seminarHallService.getPublicSeminarHalls();
        return ResponseEntity.ok(ApiResponse.success("Seminar halls retrieved", response));
    }

    // ─── Events ──────────────────────────────────────────────────────────────────

    @PostMapping("/events")
    @Operation(summary = "Create campus event", description = "Create a campus-level main event")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        EventResponse response = eventService.createEvent(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Event created", response));
    }

    @GetMapping("/events")
    @Operation(summary = "List campus events", description = "Get all campus-level events")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Events retrieved")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getCampusEvents() {
        List<EventResponse> response = eventService.getEventsByLevel("CAMPUS");
        return ResponseEntity.ok(ApiResponse.success("Events retrieved", response));
    }

    @GetMapping("/events/{eventId}/participants")
    @Operation(summary = "List event participants", description = "List event participants")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Participants retrieved")
    })
    public ResponseEntity<ApiResponse<List<EventParticipantResponse>>> getEventParticipants(
            @PathVariable UUID eventId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Participants retrieved",
                eventRegistrationService.getParticipants(eventId, user.getId())));
    }

    @PutMapping("/events/{eventId}/status")
    @Operation(summary = "Update event status", description = "Update event status")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event status updated")
    })
    public ResponseEntity<ApiResponse<EventResponse>> updateEventStatus(
            @PathVariable UUID eventId, @Valid @RequestBody UpdateEventStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Event status updated",
                eventService.updateEventStatus(eventId, request.getStatus(), user.getId())));
    }

    // ─── Broadcasts ──────────────────────────────────────────────────────────────

    @PostMapping("/broadcasts")
    @Operation(summary = "Broadcast campus message", description = "Broadcast a campus-level message via FCM")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Broadcast sent"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<BroadcastResponse>> createBroadcast(
            @Valid @RequestBody BroadcastRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        BroadcastResponse response = broadcastService.createBroadcast(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Broadcast sent", response));
    }

    // ─── Dashboard ───────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard stats", description = "Monitor stats: colleges, users, events, registrations")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stats retrieved")
    })
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse response = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", response));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}