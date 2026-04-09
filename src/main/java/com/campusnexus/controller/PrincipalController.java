package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.User;
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
@RequestMapping("/api/principal")
@PreAuthorize("hasRole('PRINCIPAL')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Principal", description = "Principal management endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PrincipalController {

    private final DepartmentService departmentService;
    private final ClubService clubService;
    private final SeminarHallService seminarHallService;
    private final EventService eventService;
    private final BroadcastService broadcastService;
    private final UserRepository userRepository;
    private final EventRegistrationService eventRegistrationService;

    public PrincipalController(DepartmentService departmentService,
                               ClubService clubService,
                               SeminarHallService seminarHallService,
                               EventService eventService,
                               BroadcastService broadcastService,
                               UserRepository userRepository,
                               EventRegistrationService eventRegistrationService) {
        this.departmentService = departmentService;
        this.clubService = clubService;
        this.seminarHallService = seminarHallService;
        this.eventService = eventService;
        this.broadcastService = broadcastService;
        this.userRepository = userRepository;
        this.eventRegistrationService = eventRegistrationService;
    }

    @PostMapping("/departments")
    @Operation(summary = "Add department", description = "Add department to own college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Department created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Department exists")
    })
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        request.setCollegeId(user.getCollege().getId());
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success("Department created", response));
    }

    @GetMapping("/departments")
    @Operation(summary = "List departments", description = "List departments in own college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Departments retrieved")
    })
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<DepartmentResponse> response = departmentService.getDepartmentsByCollege(user.getCollege().getId());
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved", response));
    }

    @PutMapping("/departments/{id}/assign-hod")
    @Operation(summary = "Assign HOD", description = "Assign HOD from professors of the department")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "HOD assigned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<ApiResponse<DepartmentResponse>> assignHOD(
            @PathVariable UUID id, @Valid @RequestBody AssignHODRequest request) {
        DepartmentResponse response = departmentService.assignHOD(id, request);
        return ResponseEntity.ok(ApiResponse.success("HOD assigned", response));
    }

    @GetMapping("/professors")
    @Operation(summary = "List professors", description = "List all professors in own college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Professors retrieved")
    })
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> getProfessors(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<UserSearchResponse> professors = userRepository
                .findByRoleAndCollegeId(com.campusnexus.enums.Role.PROFESSOR, user.getCollege().getId())
                .stream()
                .map(p -> UserSearchResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .email(p.getEmail())
                        .phone(p.getPhone())
                        .role(p.getRole().name())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Professors retrieved", professors));
    }

    @PostMapping("/club-requests/{clubId}/approve")
    @Operation(summary = "Approve club", description = "Approve club at principal level")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Club approved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<ApiResponse<ClubResponse>> approveClub(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        ClubResponse response = clubService.approveByPrincipal(clubId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Club approved", response));
    }

    @PostMapping("/club-requests/{clubId}/reject")
    @Operation(summary = "Reject club", description = "Reject club request")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Club rejected"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<ApiResponse<ClubResponse>> rejectClub(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        ClubResponse response = clubService.rejectByPrincipal(clubId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Club rejected", response));
    }

    @GetMapping("/club-requests")
    @Operation(summary = "List club requests", description = "List club requests with optional status filter")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Clubs retrieved")
    })
    public ResponseEntity<ApiResponse<List<ClubResponse>>> getClubRequests(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<ClubResponse> response = clubService.getClubRequestsForPrincipal(user.getCollege().getId(), status);
        return ResponseEntity.ok(ApiResponse.success("Club requests retrieved", response));
    }

    @PostMapping("/seminar-halls")
    @Operation(summary = "Create seminar hall", description = "Create private seminar hall for college")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hall created")
    })
    public ResponseEntity<ApiResponse<SeminarHallResponse>> createSeminarHall(
            @Valid @RequestBody SeminarHallRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        // Inject collegeId and hallType server-side
        request.setCollegeId(user.getCollege().getId());
        request.setHallType(SeminarHallType.PRIVATE);
        SeminarHallResponse response = seminarHallService.createSeminarHall(request);
        return ResponseEntity.ok(ApiResponse.success("Seminar hall created", response));
    }

    @GetMapping("/seminar-halls")
    @Operation(summary = "List college seminar halls", description = "List college seminar halls")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Halls retrieved")
    })
    public ResponseEntity<ApiResponse<List<SeminarHallResponse>>> getSeminarHalls(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<SeminarHallResponse> response = seminarHallService.getSeminarHallsByCollege(user.getCollege().getId());
        return ResponseEntity.ok(ApiResponse.success("Seminar halls retrieved", response));
    }

    @PutMapping("/seminar-halls/{id}")
    @Operation(summary = "Update seminar hall", description = "Update seminar hall details")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hall updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Hall not found")
    })
    public ResponseEntity<ApiResponse<SeminarHallResponse>> updateSeminarHall(
            @PathVariable UUID id, @Valid @RequestBody SeminarHallRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        // Re-inject collegeId on update as well
        request.setCollegeId(user.getCollege().getId());
        request.setHallType(SeminarHallType.PRIVATE);
        SeminarHallResponse response = seminarHallService.updateSeminarHall(id, request);
        return ResponseEntity.ok(ApiResponse.success("Seminar hall updated", response));
    }

    @PostMapping("/events")
    @Operation(summary = "Create college event", description = "Create college-level event")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event created")
    })
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        request.setEventLevel(com.campusnexus.enums.EventLevel.COLLEGE);
        EventResponse response = eventService.createEvent(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Event created", response));
    }

    @GetMapping("/events")
    @Operation(summary = "List college events", description = "List college-level events")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Events retrieved")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<EventResponse> response = eventService.getEventsByCollege(user.getCollege().getId());
        return ResponseEntity.ok(ApiResponse.success("Events retrieved", response));
    }

    @PutMapping("/events/{id}/approve")
    @Operation(summary = "Approve event", description = "Approve event")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event approved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponse<EventResponse>> approveEvent(@PathVariable UUID id) {
        EventResponse response = eventService.approveEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event approved", response));
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

    @PostMapping("/broadcasts")
    @Operation(summary = "Broadcast college message", description = "Broadcast college-level message")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Broadcast sent")
    })
    public ResponseEntity<ApiResponse<BroadcastResponse>> createBroadcast(
            @Valid @RequestBody BroadcastRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        BroadcastResponse response = broadcastService.createBroadcast(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Broadcast sent", response));
    }

    @GetMapping("/broadcasts")
    @Operation(summary = "List broadcasts", description = "List own broadcasts")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Broadcasts retrieved")
    })
    public ResponseEntity<ApiResponse<List<BroadcastResponse>>> getBroadcasts(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<BroadcastResponse> response = broadcastService.getBroadcastsBySender(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Broadcasts retrieved", response));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}