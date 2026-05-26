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
@RequestMapping("/api/professor")
@PreAuthorize("hasRole('PROFESSOR')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Professor", description = "Professor management endpoints")
public class ProfessorController {

    private final BatchService batchService;
    private final SubmissionService submissionService;
    private final NotesService notesService;
    private final TeacherAvailabilityService availabilityService;
    private final EventService eventService;
    private final StudentProgressService progressService;
    private final UserRepository userRepository;
    private final EventRegistrationService eventRegistrationService;
    private final TimetableService timetableService;

    public ProfessorController(BatchService batchService, SubmissionService submissionService,
            NotesService notesService, TeacherAvailabilityService availabilityService,
            EventService eventService, StudentProgressService progressService,
            UserRepository userRepository, EventRegistrationService eventRegistrationService,
            TimetableService timetableService) {
        this.batchService = batchService;
        this.submissionService = submissionService;
        this.notesService = notesService;
        this.availabilityService = availabilityService;
        this.eventService = eventService;
        this.progressService = progressService;
        this.userRepository = userRepository;
        this.eventRegistrationService = eventRegistrationService;
        this.timetableService = timetableService;
    }

    @PostMapping("/batches")
    @Operation(summary = "Create batch")
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(
            @Valid @RequestBody BatchCreateRequest request, @AuthenticationPrincipal UserDetails ud) {
        User professor = getUser(ud);
        return ResponseEntity.ok(ApiResponse.success("Batch created",
                batchService.createBatch(request, professor)));
    }

    @GetMapping("/batches")
    @Operation(summary = "List own batches")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatches(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Batches retrieved",
                batchService.getBatchesByProfessor(getUser(ud).getId())));
    }

    @PostMapping("/batches/{batchId}/sections")
    @Operation(summary = "Create section")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(
            @PathVariable UUID batchId, @Valid @RequestBody BatchSectionCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Section created",
                batchService.createSection(batchId, request)));
    }

    @GetMapping("/batches/{batchId}/sections")
    @Operation(summary = "List sections of batch")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getSections(@PathVariable UUID batchId) {
        return ResponseEntity.ok(ApiResponse.success("Sections retrieved",
                batchService.getSectionsByBatch(batchId)));
    }

    @PutMapping("/batches/{batchId}")
    @Operation(summary = "Update batch")
    public ResponseEntity<ApiResponse<BatchResponse>> updateBatch(
            @PathVariable UUID batchId, @Valid @RequestBody BatchCreateRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Batch updated",
                batchService.updateBatch(batchId, request, getUser(ud))));
    }

    @DeleteMapping("/batches/{batchId}")
    @Operation(summary = "Delete batch")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @PathVariable UUID batchId, @AuthenticationPrincipal UserDetails ud) {
        batchService.deleteBatch(batchId, getUser(ud));
        return ResponseEntity.ok(ApiResponse.success("Batch deleted"));
    }

    @PutMapping("/sections/{sectionId}")
    @Operation(summary = "Update section")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @PathVariable UUID sectionId, @Valid @RequestBody BatchSectionCreateRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Section updated",
                batchService.updateSection(sectionId, request, getUser(ud))));
    }

    @DeleteMapping("/sections/{sectionId}")
    @Operation(summary = "Delete section")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @PathVariable UUID sectionId, @AuthenticationPrincipal UserDetails ud) {
        batchService.deleteSection(sectionId, getUser(ud));
        return ResponseEntity.ok(ApiResponse.success("Section deleted"));
    }

    @GetMapping("/submissions/{sectionId}")
    @Operation(summary = "View submissions for section")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(@PathVariable UUID sectionId) {
        return ResponseEntity.ok(ApiResponse.success("Submissions retrieved",
                submissionService.getSubmissionsBySection(sectionId)));
    }

    @PutMapping("/submissions/{submissionId}/remark")
    @Operation(summary = "Give remark on submission")
    public ResponseEntity<ApiResponse<SubmissionResponse>> addRemark(
            @PathVariable UUID submissionId, @Valid @RequestBody SubmissionRemarkRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Remark added",
                submissionService.addRemark(submissionId, request, getUser(ud).getId())));
    }

    @PostMapping("/notes")
    @Operation(summary = "Upload notes")
    public ResponseEntity<ApiResponse<NotesResponse>> uploadNotes(
            @Valid @RequestBody NotesUploadRequest request, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Notes uploaded",
                notesService.uploadNotes(request, getUser(ud).getId())));
    }

    @GetMapping("/notes")
    @Operation(summary = "List own notes")
    public ResponseEntity<ApiResponse<List<NotesResponse>>> getNotes(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved",
                notesService.getNotesByProfessor(getUser(ud).getId())));
    }

    @PutMapping("/notes/{id}")
    @Operation(summary = "Update note")
    public ResponseEntity<ApiResponse<NotesResponse>> updateNote(
            @PathVariable UUID id, @Valid @RequestBody NotesUploadRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Note updated",
                notesService.updateNotes(id, request, getUser(ud).getId())));
    }

    @DeleteMapping("/notes/{id}")
    @Operation(summary = "Delete note")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails ud) {
        notesService.deleteNotes(id, getUser(ud).getId());
        return ResponseEntity.ok(ApiResponse.success("Note deleted"));
    }

    @PostMapping("/availability")
    @Operation(summary = "Mark availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> createAvailability(
            @Valid @RequestBody TeacherAvailabilityRequest request, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Availability created",
                availabilityService.createAvailability(request, getUser(ud).getId())));
    }

    @GetMapping("/availability")
    @Operation(summary = "View own availability")
    public ResponseEntity<ApiResponse<List<AvailabilityResponse>>> getAvailability(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Availability retrieved",
                availabilityService.getAvailabilityByTeacher(getUser(ud).getId())));
    }

    @PutMapping("/availability/{id}")
    @Operation(summary = "Update availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> updateAvailability(
            @PathVariable UUID id, @Valid @RequestBody TeacherAvailabilityRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Availability updated",
                availabilityService.updateAvailability(id, request, getUser(ud).getId())));
    }

    @DeleteMapping("/availability/{id}")
    @Operation(summary = "Delete availability")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails ud) {
        availabilityService.deleteAvailability(id, getUser(ud).getId());
        return ResponseEntity.ok(ApiResponse.success("Availability deleted"));
    }

    @PostMapping("/events")
    @Operation(summary = "Create event")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Event created",
                eventService.createEvent(request, getUser(ud).getId())));
    }

    @GetMapping("/events")
    @Operation(summary = "List own events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Events retrieved",
                eventService.getEventsByCreator(getUser(ud).getId())));
    }

    @GetMapping("/events/{eventId}/participants")
    @Operation(summary = "List event participants")
    public ResponseEntity<ApiResponse<List<EventParticipantResponse>>> getEventParticipants(
            @PathVariable UUID eventId, @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Participants retrieved",
                eventRegistrationService.getParticipants(eventId, getUser(ud).getId())));
    }

    @PutMapping("/events/{eventId}/status")
    @Operation(summary = "Update event status")
    public ResponseEntity<ApiResponse<EventResponse>> updateEventStatus(
            @PathVariable UUID eventId, @Valid @RequestBody UpdateEventStatusRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Event status updated",
                eventService.updateEventStatus(eventId, request.getStatus(), getUser(ud).getId())));
    }

    @PostMapping("/student-progress/{studentId}")
    @Operation(summary = "Update student progress")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateProgress(
            @PathVariable UUID studentId, @Valid @RequestBody StudentProgressRequest request,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success("Progress updated",
                progressService.updateProgress(studentId, request, getUser(ud).getId())));
    }

    @GetMapping("/student-progress/{studentId}")
    @Operation(summary = "View student progress history")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getProgress(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved",
                progressService.getProgressByStudent(studentId)));
    }

    // ==================== TIMETABLE ENDPOINTS ====================

    @GetMapping("/timetable")
    @Operation(summary = "View own timetable", description = "View professor's own published teaching schedule")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetable(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved",
                timetableService.getTimetableForProfessor(user.getId())));
    }

    @GetMapping("/timetable/merged")
    @Operation(summary = "View merged schedule", description = "View combined teaching schedule and availability records")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getMergedSchedule(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        return ResponseEntity.ok(ApiResponse.success("Merged schedule retrieved",
                timetableService.getTeacherScheduleMerged(user.getId())));
    }

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
