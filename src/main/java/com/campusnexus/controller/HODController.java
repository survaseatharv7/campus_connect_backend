package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.User;
import com.campusnexus.enums.SeminarHallType;
import com.campusnexus.enums.TimetableStatus;
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

@RestController
@RequestMapping("/api/hod")
@PreAuthorize("hasRole('HOD')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "HOD", description = "HOD management endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HODController {

    private final EventService eventService;
    private final ClubService clubService;
    private final TimetableService timetableService;
    private final SeminarHallService seminarHallService;
    private final BroadcastService broadcastService;
    private final UserRepository userRepository;
    private final EventRegistrationService eventRegistrationService;

    public HODController(EventService eventService,
                         ClubService clubService,
                         TimetableService timetableService,
                         SeminarHallService seminarHallService,
                         BroadcastService broadcastService,
                         UserRepository userRepository,
                         EventRegistrationService eventRegistrationService) {
        this.eventService = eventService;
        this.clubService = clubService;
        this.timetableService = timetableService;
        this.seminarHallService = seminarHallService;
        this.broadcastService = broadcastService;
        this.userRepository = userRepository;
        this.eventRegistrationService = eventRegistrationService;
    }

    @PostMapping("/events")
    @Operation(summary = "Create department event", description = "Create department-level event")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        request.setEventLevel(com.campusnexus.enums.EventLevel.DEPARTMENT);
        EventResponse response = eventService.createEvent(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Event created", response));
    }

    @GetMapping("/events")
    @Operation(summary = "List department events", description = "List department events")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Events retrieved")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<EventResponse> response = eventService.getEventsByDepartment(user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("Events retrieved", response));
    }

    @PutMapping("/events/{id}/approve")
    @Operation(summary = "Approve department event", description = "Approve department event")
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

    @PostMapping("/club-requests/{clubId}/approve")
    @Operation(summary = "Approve club at HOD level", description = "Approve club at HOD level")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Club approved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<ApiResponse<ClubResponse>> approveClub(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        ClubResponse response = clubService.approveByHOD(clubId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Club approved by HOD", response));
    }

    @PostMapping("/club-requests/{clubId}/reject")
    @Operation(summary = "Reject club at HOD level", description = "Reject club at HOD level")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Club rejected"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<ApiResponse<ClubResponse>> rejectClub(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        ClubResponse response = clubService.rejectByHOD(clubId, user.getId());
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
        List<ClubResponse> response = clubService.getClubRequestsForHOD(user.getDepartment().getId(), status);
        return ResponseEntity.ok(ApiResponse.success("Club requests retrieved", response));
    }

    // ==================== TIMETABLE ENDPOINTS ====================

    @PostMapping("/timetable")
    @Operation(summary = "Create timetable entry", description = "Create department timetable entry")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<TimetableResponse>> createTimetable(
            @Valid @RequestBody TimetableRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        TimetableResponse response = timetableService.createTimetable(request, user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("Timetable created", response));
    }

    @GetMapping("/timetable")
    @Operation(summary = "List department timetable", description = "Returns PUBLISHED and DRAFT timetable entries (excludes DELETED and ARCHIVED)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable retrieved")
    })
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetable(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<TimetableResponse> response = timetableService.getTimetableByDepartmentAndStatuses(
                user.getDepartment().getId(),
                List.of(TimetableStatus.PUBLISHED, TimetableStatus.DRAFT));
        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved", response));
    }

    @PutMapping("/timetable/{id}")
    @Operation(summary = "Update timetable entry", description = "Update timetable entry")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<ApiResponse<TimetableResponse>> updateTimetable(
            @PathVariable UUID id, @Valid @RequestBody TimetableRequest request) {
        TimetableResponse response = timetableService.updateTimetable(id, request);
        return ResponseEntity.ok(ApiResponse.success("Timetable updated", response));
    }

    @DeleteMapping("/timetable/{id}")
    @Operation(summary = "Soft delete timetable entry", description = "Soft delete timetable slot (sets status to DELETED)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteTimetable(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        timetableService.deleteTimetableSlot(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Timetable slot deleted"));
    }

    @PostMapping("/timetable/ai-suggest")
    @Operation(summary = "AI timetable suggestion", description = "Generate AI-powered timetable suggestions using Groq LLM")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suggestions generated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "AI error or invalid input")
    })
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> aiSuggest(
            @Valid @RequestBody AISuggestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<TimetableResponse> response = timetableService.generateAISuggestion(
                request, user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("AI suggestions generated", response));
    }

    @PostMapping("/timetable/publish")
    @Operation(summary = "Publish timetable", description = "Publish timetable slots after final clash check")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable published"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Conflict detected")
    })
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> publishTimetable(
            @Valid @RequestBody List<TimetableRequest> slots,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<TimetableResponse> response = timetableService.publishTimetable(
                slots, user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("Timetable published successfully", response));
    }

    @PutMapping("/timetable/archive")
    @Operation(summary = "Archive semester timetable", description = "Bulk archive all PUBLISHED slots for a semester")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Timetable archived"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No slots found")
    })
    public ResponseEntity<ApiResponse<Void>> archiveTimetable(
            @RequestParam String year,
            @RequestParam int semester,
            @RequestParam String division,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        timetableService.archiveSemester(user.getDepartment().getId(), year, semester, division);
        return ResponseEntity.ok(ApiResponse.success("Timetable archived successfully"));
    }

    @GetMapping("/timetable/archived")
    @Operation(summary = "List archived timetable", description = "Returns ARCHIVED timetable entries for the HOD's department")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archived timetable retrieved")
    })
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getArchivedTimetable(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<TimetableResponse> response = timetableService.getArchivedByDepartment(user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("Archived timetable retrieved", response));
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
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Professors retrieved", professors));
    }

    // ==================== SEMINAR HALL ENDPOINTS ====================

    @PostMapping("/seminar-halls")
    @Operation(summary = "Manage dept seminar halls", description = "Create department seminar hall")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hall created")
    })
    public ResponseEntity<ApiResponse<SeminarHallResponse>> createSeminarHall(
            @Valid @RequestBody SeminarHallRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        // Inject collegeId, departmentId and hallType server-side from authenticated HOD
        request.setCollegeId(user.getCollege().getId());
        request.setDepartmentId(user.getDepartment().getId());
        request.setHallType(SeminarHallType.PRIVATE);
        SeminarHallResponse response = seminarHallService.createSeminarHall(request);
        return ResponseEntity.ok(ApiResponse.success("Seminar hall created", response));
    }

    @GetMapping("/seminar-halls")
    @Operation(summary = "List dept seminar halls", description = "List department seminar halls")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Halls retrieved")
    })
    public ResponseEntity<ApiResponse<List<SeminarHallResponse>>> getSeminarHalls(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        List<SeminarHallResponse> response = seminarHallService
                .getSeminarHallsByDepartment(user.getDepartment().getId());
        return ResponseEntity.ok(ApiResponse.success("Seminar halls retrieved", response));
    }

    @PostMapping("/broadcasts")
    @Operation(summary = "Broadcast department message", description = "Broadcast department-level message")
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

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}