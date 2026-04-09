package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.User;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Student", description = "Student endpoints")
public class StudentController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final ClubService clubService;
    private final SubmissionService submissionService;
    private final NotesService notesService;
    private final BroadcastService broadcastService;
    private final TimetableService timetableService;
    private final TeacherAvailabilityService availabilityService;
    private final StudentProgressService progressService;
    private final BatchService batchService;
    private final UserRepository userRepository;

    public StudentController(EventService eventService, EventRegistrationService registrationService,
            ClubService clubService, SubmissionService submissionService,
            NotesService notesService, BroadcastService broadcastService,
            TimetableService timetableService, TeacherAvailabilityService availabilityService,
            StudentProgressService progressService, BatchService batchService,
            UserRepository userRepository) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.clubService = clubService;
        this.submissionService = submissionService;
        this.notesService = notesService;
        this.broadcastService = broadcastService;
        this.timetableService = timetableService;
        this.availabilityService = availabilityService;
        this.progressService = progressService;
        this.batchService = batchService;
        this.userRepository = userRepository;
    }

    @GetMapping("/events")
    @Operation(summary = "List visible events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        UUID collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        List<EventResponse> events = eventService.getVisibleEventsForStudent(collegeId, deptId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Events retrieved", events));
    }

    @PostMapping("/events/{eventId}/register")
    @Operation(summary = "Register for event")
    public ResponseEntity<ApiResponse<EventRegistrationResponse>> registerForEvent(
            @PathVariable UUID eventId, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Registered",
                registrationService.registerForEvent(eventId, getUser(ud).getId())));
    }

    @GetMapping("/events/my-registrations")
    @Operation(summary = "My event registrations")
    public ResponseEntity<ApiResponse<List<EventRegistrationResponse>>> getRegistrations(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved",
                registrationService.getStudentRegistrations(getUser(ud).getId())));
    }

    @GetMapping("/events/{eventId}/ticket")
    @Operation(summary = "Get ticket details")
    public ResponseEntity<ApiResponse<EventRegistrationResponse>> getTicket(
            @PathVariable UUID eventId, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Ticket retrieved",
                registrationService.getTicketDetails(eventId, getUser(ud).getId())));
    }

    @PostMapping("/clubs")
    @Operation(summary = "Request club creation")
    public ResponseEntity<ApiResponse<ClubResponse>> createClub(
            @Valid @RequestBody ClubCreateRequest request, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Club request submitted",
                clubService.createClub(request, getUser(ud).getId())));
    }

    @GetMapping("/clubs")
    @Operation(summary = "List approved clubs")
    public ResponseEntity<ApiResponse<List<ClubResponse>>> getClubs(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        UUID collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        return ResponseEntity.ok(ApiResponse.success("Clubs retrieved",
                clubService.getClubsByCollege(collegeId)));
    }

    @PostMapping("/clubs/{clubId}/join")
    @Operation(summary = "Join club")
    public ResponseEntity<ApiResponse<ClubResponse>> joinClub(
            @PathVariable UUID clubId, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Joined club",
                clubService.joinClub(clubId, getUser(ud).getId())));
    }

    @PostMapping("/submissions/{sectionId}")
    @Operation(summary = "Submit work")
    public ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @PathVariable UUID sectionId, @Valid @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Submission created",
                submissionService.createSubmission(sectionId, request, getUser(ud).getId())));
    }

    @GetMapping("/submissions")
    @Operation(summary = "My submissions")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Submissions retrieved",
                submissionService.getSubmissionsByStudent(getUser(ud).getId())));
    }

    @GetMapping("/sections")
    @Operation(summary = "List relevant sections", description = "List all sections belonging to student's department batches")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getSections(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Sections retrieved",
                batchService.getSectionsForStudent(getUser(ud).getId())));
    }

    @GetMapping("/notes")
    @Operation(summary = "Browse notes")
    public ResponseEntity<ApiResponse<List<NotesResponse>>> getNotes(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String subject) {
        User user = getUser(ud);
        UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved",
                notesService.getNotesByDepartment(deptId, year, subject)));
    }

    @GetMapping("/broadcasts")
    @Operation(summary = "View broadcasts")
    public ResponseEntity<ApiResponse<List<BroadcastResponse>>> getBroadcasts(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        UUID collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        return ResponseEntity.ok(ApiResponse.success("Broadcasts retrieved",
                broadcastService.getRelevantBroadcasts(collegeId, deptId)));
    }

    @GetMapping("/timetable")
    @Operation(summary = "View timetable")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetable(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved",
                timetableService.getTimetableByDepartment(deptId)));
    }

    @GetMapping("/teacher-availability")
    @Operation(summary = "View teacher availability")
    public ResponseEntity<ApiResponse<List<AvailabilityResponse>>> getTeacherAvailability(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        return ResponseEntity.ok(ApiResponse.success("Availability retrieved",
                availabilityService.getAvailabilityByDepartment(deptId)));
    }

    @GetMapping("/progress")
    @Operation(summary = "View own progress")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getProgress(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved",
                progressService.getProgressByStudent(getUser(ud).getId())));
    }

    @GetMapping("/professors")
    @Operation(summary = "List professors", description = "List all professors in own college")
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> getProfessors(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        UUID collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        List<UserSearchResponse> professors = userRepository
                .findByRoleAndCollegeId(com.campusnexus.enums.Role.PROFESSOR, collegeId)
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

    @GetMapping("/students")
    @Operation(summary = "List students", description = "List all students in own college")
    public ResponseEntity<ApiResponse<List<User>>> getStudents(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved",
                userRepository.findByRoleAndCollegeId(com.campusnexus.enums.Role.STUDENT, user.getCollege().getId())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request, @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getPhone() != null)
            user.setPhone(request.getPhone());
        if (request.getProfilePicUrl() != null)
            user.setProfilePicUrl(request.getProfilePicUrl());
        if (request.getFcmToken() != null)
            user.setFcmToken(request.getFcmToken());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated"));
    }

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
