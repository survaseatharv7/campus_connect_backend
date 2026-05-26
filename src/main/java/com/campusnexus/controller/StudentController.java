package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.User;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.StudentProfileRepository;
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
        private final StudentProfileService profileService;
        private final CloudinaryService cloudinaryService;
        private final StudentProfileRepository studentProfileRepository;
        private final UserRepository userRepository;

        public StudentController(EventService eventService, EventRegistrationService registrationService,
                        ClubService clubService, SubmissionService submissionService,
                        NotesService notesService, BroadcastService broadcastService,
                        TimetableService timetableService, TeacherAvailabilityService availabilityService,
                        StudentProgressService progressService, BatchService batchService,
                        StudentProfileService profileService,
                        CloudinaryService cloudinaryService,
                        StudentProfileRepository studentProfileRepository,
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
                this.profileService = profileService;
                this.cloudinaryService = cloudinaryService;
                this.studentProfileRepository = studentProfileRepository;
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
                        @AuthenticationPrincipal UserDetails ud) {
                User user = getUser(ud);
                UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;

                java.util.Optional<com.campusnexus.entity.StudentProfile> profileOpt = studentProfileRepository.findByUserId(user.getId());
                if (profileOpt.isPresent()) {
                        com.campusnexus.entity.StudentProfile profile = profileOpt.get();
                        if (profile.getYear() != null && profile.getSemester() != null) {
                                List<NotesResponse> notes = notesService.getNotesByYearSemesterDivision(
                                                profile.getYear(),
                                                profile.getSemester(),
                                                profile.getDivision(),
                                                deptId
                                );
                                return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
                        }
                }

                return ResponseEntity.ok(ApiResponse.success("Notes retrieved",
                                notesService.getNotesByDepartment(deptId, null, null)));
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
        @Operation(summary = "View timetable", description = "View published timetable for specific year, semester, and division")
        public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetable(
                        @RequestParam(required = false) String year,
                        @RequestParam(required = false) Integer semester,
                        @RequestParam(required = false) String division,
                        @AuthenticationPrincipal UserDetails ud) {
                System.out.println("Student timetable endpoint called");
                User user = getUser(ud);
                UUID deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;

                String targetYear = year;
                Integer targetSemester = semester;
                String targetDivision = division;

                if (targetYear == null || targetSemester == null || targetDivision == null) {
                        java.util.Optional<com.campusnexus.entity.StudentProfile> profileOpt = studentProfileRepository.findByUserId(user.getId());
                        if (profileOpt.isPresent()) {
                                com.campusnexus.entity.StudentProfile profile = profileOpt.get();
                                if (targetYear == null && profile.getYear() != null) {
                                        targetYear = String.valueOf(profile.getYear());
                                }
                                if (targetSemester == null && profile.getSemester() != null) {
                                        targetSemester = profile.getSemester();
                                }
                                if (targetDivision == null && profile.getDivision() != null) {
                                        targetDivision = profile.getDivision();
                                }
                        }
                }

                if (targetYear == null || targetSemester == null || targetDivision == null) {
                        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved", List.of()));
                }

                List<TimetableResponse> timetable = timetableService.getTimetableForStudent(deptId, targetYear, targetSemester, targetDivision);
                return ResponseEntity.ok(ApiResponse.success("Timetable retrieved", timetable));
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
                                userRepository.findByRoleAndCollegeId(com.campusnexus.enums.Role.STUDENT,
                                                user.getCollege().getId())));
        }

        @GetMapping("/profile")
        @Operation(summary = "Get detailed profile")
        public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(@AuthenticationPrincipal UserDetails ud) {
                User user = getUser(ud);
                System.out.println("Fetching student profile");
                System.out.println("Authenticated user: " + user.getEmail());
                return ResponseEntity.ok(ApiResponse.success("Profile retrieved",
                                profileService.getStudentProfile(user.getId())));
        }

        @PutMapping("/profile")
        @Operation(summary = "Update detailed profile")
        public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
                        @Valid @RequestBody UpdateStudentProfileRequest request,
                        @AuthenticationPrincipal UserDetails ud) {
                return ResponseEntity.ok(ApiResponse.success("Profile updated",
                                profileService.updateStudentProfile(getUser(ud).getId(), request)));
        }

        @PostMapping("/upload/profile-pic")
        @Operation(summary = "Upload profile picture")
        public ResponseEntity<ApiResponse<java.util.Map<String, String>>> uploadProfilePic(
                        @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                        @AuthenticationPrincipal UserDetails ud) {

                String contentType = file.getContentType();
                if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                        throw new com.campusnexus.exception.BadRequestException("Only JPG and PNG images are allowed");
                }

                String url = cloudinaryService.uploadFile(file, "campusnexus/profiles", "image");

                User user = getUser(ud);
                user.setProfilePicUrl(url);
                userRepository.save(user);

                return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded", java.util.Map.of("url", url)));
        }

        @PostMapping("/upload/resume")
        @Operation(summary = "Upload resume")
        public ResponseEntity<ApiResponse<java.util.Map<String, String>>> uploadResume(
                        @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                        @AuthenticationPrincipal UserDetails ud) {

                String contentType = file.getContentType();
                if (contentType == null || !contentType.equals("application/pdf")) {
                        throw new com.campusnexus.exception.BadRequestException("Only PDF files are allowed");
                }

                String url = cloudinaryService.uploadFile(file, "campusnexus/resumes", "raw");

                User user = getUser(ud);
                com.campusnexus.entity.StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                                .orElseGet(() -> com.campusnexus.entity.StudentProfile.builder().user(user).build());
                profile.setResumeUrl(url);
                studentProfileRepository.save(profile);

                return ResponseEntity.ok(ApiResponse.success("Resume uploaded", java.util.Map.of("url", url)));
        }

        private User getUser(UserDetails ud) {
                return userRepository.findByEmail(ud.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
}
