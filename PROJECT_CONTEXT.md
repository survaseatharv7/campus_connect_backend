# CampusNexus — Project Context & Change Log

## Project Overview
- Name: CampusNexus
- Type: Multi-College Campus Management System
- Backend: Spring Boot 4.0.3, Java 17, PostgreSQL
- Started: 2026-03-11

---

## Architecture Summary
- Package: com.campusnexus
- Database: PostgreSQL (campus_nexus)
- Auth: JWT (jjwt 0.12.6) with Spring Security 7
- File Storage: Firebase Storage
- Notifications: Firebase FCM + Spring Mail
- Payments: Stripe
- API Docs: Springdoc OpenAPI / Swagger UI at /swagger-ui.html

---

## Modules
| Module | Role | Base Path |
|--------|------|-----------|
| Campus Admin | CAMPUS_ADMIN | /api/admin |
| Principal | PRINCIPAL | /api/principal |
| HOD | HOD | /api/hod |
| Professor | PROFESSOR | /api/professor |
| Student | STUDENT | /api/student |
| Auth | All | /api/auth |
| Webhook | System | /api/webhook |

---

## File Registry

| # | File Path | Type | Status | Description |
|---|-----------|------|--------|-------------|
| 1 | pom.xml | Config | ✏️ Modified | Maven build config, let Spring Boot parent manage compiler settings and Lombok annotation processing |
| 2 | src/main/resources/application.properties | Config | ✏️ Modified | All config properties + ngrok/CORS props |
| 3 | src/main/java/com/campusnexus/CampusNexusApplication.java | Main | ✅ Created | Spring Boot main class |
| 4 | src/main/java/com/campusnexus/enums/Role.java | Enum | ✅ Created | CAMPUS_ADMIN, PRINCIPAL, HOD, PROFESSOR, STUDENT |
| 5 | src/main/java/com/campusnexus/enums/CollegeStatus.java | Enum | ✅ Created | PENDING, ACTIVE, INACTIVE |
| 6 | src/main/java/com/campusnexus/enums/EventStatus.java | Enum | ✅ Created | UPCOMING, ONGOING, COMPLETED, CANCELLED |
| 7 | src/main/java/com/campusnexus/enums/EventLevel.java | Enum | ✅ Created | CAMPUS, COLLEGE, DEPARTMENT, CLUB |
| 8 | src/main/java/com/campusnexus/enums/EventType.java | Enum | ✅ Created | MAIN, SUB |
| 9 | src/main/java/com/campusnexus/enums/ClubStatus.java | Enum | ✅ Created | PENDING_HOD, PENDING_PRINCIPAL, APPROVED, REJECTED |
| 10 | src/main/java/com/campusnexus/enums/BatchSectionType.java | Enum | ✅ Created | PROJECT, SEMINAR, INTERNSHIP |
| 11 | src/main/java/com/campusnexus/enums/SubmissionType.java | Enum | ✅ Created | INDIVIDUAL, TEAM |
| 12 | src/main/java/com/campusnexus/enums/SubmissionStatus.java | Enum | ✅ Created | SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED |
| 13 | src/main/java/com/campusnexus/enums/SeminarHallType.java | Enum | ✅ Created | PUBLIC, PRIVATE |
| 14 | src/main/java/com/campusnexus/enums/SeminarHallStatus.java | Enum | ✅ Created | AVAILABLE, BOOKED, MAINTENANCE |
| 15 | src/main/java/com/campusnexus/enums/BroadcastLevel.java | Enum | ✅ Created | CAMPUS, COLLEGE, DEPARTMENT |
| 16 | src/main/java/com/campusnexus/enums/TicketStatus.java | Enum | ✅ Created | PENDING, CONFIRMED, CANCELLED |
| 17 | src/main/java/com/campusnexus/enums/PaymentStatus.java | Enum | ✅ Created | PENDING, SUCCESS, FAILED |
| 18 | src/main/java/com/campusnexus/enums/TeacherAvailabilityStatus.java | Enum | ✅ Created | AVAILABLE, BUSY, ON_LEAVE |
| 19 | src/main/java/com/campusnexus/entity/User.java | Entity | ✅ Created | User entity with all roles |
| 20 | src/main/java/com/campusnexus/entity/College.java | Entity | ✅ Created | College with unique code, principal |
| 21 | src/main/java/com/campusnexus/entity/Department.java | Entity | ✅ Created | Department with college, HOD |
| 22 | src/main/java/com/campusnexus/entity/Batch.java | Entity | ✅ Created | Batch with department, professor |
| 23 | src/main/java/com/campusnexus/entity/BatchSection.java | Entity | ✏️ Modified | Added deadline field |
| 24 | src/main/java/com/campusnexus/entity/Submission.java | Entity | ✅ Created | Submission with team and review |
| 25 | src/main/java/com/campusnexus/entity/Club.java | Entity | ✅ Created | Club with approval chain |
| 26 | src/main/java/com/campusnexus/entity/Event.java | Entity | ✏️ Modified | Added openToExternal field and @JsonIgnore to break cycles, made nullable to support database auto-update |
| 27 | src/main/java/com/campusnexus/entity/EventRegistration.java | Entity | ✏️ Modified | Added @JsonIgnore to student and event |
| 28 | src/main/java/com/campusnexus/entity/SeminarHall.java | Entity | ✅ Created | Seminar hall with type/capacity |
| 29 | src/main/java/com/campusnexus/entity/SeminarHallBooking.java | Entity | ✅ Created | Hall booking with time range |
| 30 | src/main/java/com/campusnexus/entity/Broadcast.java | Entity | ✅ Created | Multi-level broadcast messages |
| 31 | src/main/java/com/campusnexus/entity/Notes.java | Entity | ✏️ Modified | Professor notes uploads with nullable year/sem and new division |
| 32 | src/main/java/com/campusnexus/entity/TeacherAvailability.java | Entity | ✅ Created | Teacher availability slots |
| 33 | src/main/java/com/campusnexus/entity/Timetable.java | Entity | ✅ Created | Department timetable |
| 34 | src/main/java/com/campusnexus/entity/StudentProgress.java | Entity | ✅ Created | Student academic progress |
| 35 | src/main/java/com/campusnexus/entity/InvalidatedToken.java | Entity | ✅ Created | JWT blocklist for logout |
| 36 | src/main/java/com/campusnexus/repository/UserRepository.java | Repository | ✅ Created | User queries |
| 37 | src/main/java/com/campusnexus/repository/CollegeRepository.java | Repository | ✅ Created | College queries |
| 38 | src/main/java/com/campusnexus/repository/DepartmentRepository.java | Repository | ✅ Created | Department queries |
| 39 | src/main/java/com/campusnexus/repository/BatchRepository.java | Repository | ✅ Created | Batch queries |
| 40 | src/main/java/com/campusnexus/repository/BatchSectionRepository.java | Repository | ✏️ Modified | Added findByBatchDepartmentId query |
| 41 | src/main/java/com/campusnexus/repository/SubmissionRepository.java | Repository | ✏️ Modified | Added team-aware existence check for pending tasks |
| 42 | src/main/java/com/campusnexus/repository/ClubRepository.java | Repository | ✅ Created | Club queries with status filter |
| 43 | src/main/java/com/campusnexus/repository/EventRepository.java | Repository | ✏️ Modified | Added sorting by startDateTime DESC and openToExternal status queries |
| 44 | src/main/java/com/campusnexus/repository/EventRegistrationRepository.java | Repository | ✅ Created | Registration queries with Stripe |
| 45 | src/main/java/com/campusnexus/repository/SeminarHallRepository.java | Repository | ✅ Created | SeminarHall queries |
| 46 | src/main/java/com/campusnexus/repository/SeminarHallBookingRepository.java | Repository | ✅ Created | Booking queries |
| 47 | src/main/java/com/campusnexus/repository/BroadcastRepository.java | Repository | ✅ Created | Broadcast queries with JPQL |
| 48 | src/main/java/com/campusnexus/repository/NotesRepository.java | Repository | ✏️ Modified | Notes queries with division filters |
| 49 | src/main/java/com/campusnexus/repository/TeacherAvailabilityRepository.java | Repository | ✅ Created | Availability queries |
| 50 | src/main/java/com/campusnexus/repository/TimetableRepository.java | Repository | ✏️ Modified | Timetable queries with findPublishedForStudent method |
| 51 | src/main/java/com/campusnexus/repository/StudentProgressRepository.java | Repository | ✅ Created | Progress queries |
| 52 | src/main/java/com/campusnexus/repository/InvalidatedTokenRepository.java | Repository | ✅ Created | Token blocklist check |
| 53 | src/main/java/com/campusnexus/dto/LoginRequest.java | DTO | ✅ Created | Login DTO |
| 54 | src/main/java/com/campusnexus/dto/RegisterRequest.java | DTO | ✅ Created | Register DTO |
| 55 | src/main/java/com/campusnexus/dto/RefreshTokenRequest.java | DTO | ✅ Created | Refresh token DTO |
| 56 | src/main/java/com/campusnexus/dto/CollegeCreateRequest.java | DTO | ✅ Created | College creation |
| 57 | src/main/java/com/campusnexus/dto/DepartmentCreateRequest.java | DTO | ✅ Created | Department creation |
| 58 | src/main/java/com/campusnexus/dto/AssignPrincipalRequest.java | DTO | ✅ Created | Principal assignment |
| 59 | src/main/java/com/campusnexus/dto/AssignHODRequest.java | DTO | ✅ Created | HOD assignment |
| 60 | src/main/java/com/campusnexus/dto/EventCreateRequest.java | DTO | ✏️ Modified | Added @DecimalMin("0.00") and @Digits on ticketPrice; blocks negative prices at validation layer |
| 60.1 | src/main/java/com/campusnexus/dto/UpdateEventStatusRequest.java | DTO | ✅ Created | Update event status |
| 61 | src/main/java/com/campusnexus/dto/EventRegistrationRequest.java | DTO | ✅ Created | Event registration |
| 62 | src/main/java/com/campusnexus/dto/ClubCreateRequest.java | DTO | ✅ Created | Club creation |
| 63 | src/main/java/com/campusnexus/dto/BatchCreateRequest.java | DTO | ✏️ Modified | Removed departmentId field |
| 64 | src/main/java/com/campusnexus/dto/BatchSectionCreateRequest.java | DTO | ✏️ Modified | Added deadline and isActive fields |
| 65 | src/main/java/com/campusnexus/dto/SubmissionRequest.java | DTO | ✅ Created | Submission creation |
| 66 | src/main/java/com/campusnexus/dto/SubmissionRemarkRequest.java | DTO | ✅ Created | Professor remark |
| 67 | src/main/java/com/campusnexus/dto/NotesUploadRequest.java | DTO | ✏️ Modified | Notes upload request with optional division |
| 68 | src/main/java/com/campusnexus/dto/BroadcastRequest.java | DTO | ✅ Created | Broadcast creation |
| 69 | src/main/java/com/campusnexus/dto/SeminarHallRequest.java | DTO | ✅ Created | Hall creation |
| 70 | src/main/java/com/campusnexus/dto/SeminarHallBookingRequest.java | DTO | ✅ Created | Hall booking |
| 71 | src/main/java/com/campusnexus/dto/TimetableRequest.java | DTO | ✅ Created | Timetable creation |
| 72 | src/main/java/com/campusnexus/dto/TeacherAvailabilityRequest.java | DTO | ✅ Created | Availability creation |
| 73 | src/main/java/com/campusnexus/dto/StudentProgressRequest.java | DTO | ✅ Created | Progress update |
| 74 | src/main/java/com/campusnexus/dto/UpdateProfileRequest.java | DTO | ✅ Created | Profile update |
| 75 | src/main/java/com/campusnexus/dto/ApiResponse.java | DTO | ✅ Created | Generic API response wrapper |
| 76 | src/main/java/com/campusnexus/dto/LoginResponse.java | DTO | ✅ Created | Login response with tokens |
| 77 | src/main/java/com/campusnexus/dto/CollegeResponse.java | DTO | ✅ Created | College response |
| 78 | src/main/java/com/campusnexus/dto/DepartmentResponse.java | DTO | ✅ Created | Department response |
| 121 | src/main/java/com/campusnexus/dto/EventResponse.java | DTO | ✏️ Modified | Added isRegistered flag and openToExternal support |
| 80 | src/main/java/com/campusnexus/dto/EventRegistrationResponse.java | DTO | ✏️ Modified | Registration/ticket response |
| 80.1 | src/main/java/com/campusnexus/dto/EventParticipantResponse.java | DTO | ✅ Created | Participants list response |
| 81 | src/main/java/com/campusnexus/dto/ClubResponse.java | DTO | ✏️ Modified | Added isMember and isOwner flags |
| 82 | src/main/java/com/campusnexus/dto/BatchResponse.java | DTO | ✅ Created | Batch response |
| 83 | src/main/java/com/campusnexus/dto/SectionResponse.java | DTO | ✏️ Modified | Added batchName and teacherName |
| 84 | src/main/java/com/campusnexus/dto/SubmissionResponse.java | DTO | ✏️ Modified | Added teamMemberNames and teamSize fields |
| 85 | src/main/java/com/campusnexus/dto/NotesResponse.java | DTO | ✏️ Modified | Notes response with division and yearLabel |
| 86 | src/main/java/com/campusnexus/dto/BroadcastResponse.java | DTO | ✏️ Modified | Added senderRole field |
| 87 | src/main/java/com/campusnexus/dto/SeminarHallResponse.java | DTO | ✅ Created | Hall response |
| 88 | src/main/java/com/campusnexus/dto/TimetableResponse.java | DTO | ✅ Created | Timetable response |
| 89 | src/main/java/com/campusnexus/dto/AvailabilityResponse.java | DTO | ✅ Created | Availability response |
| 90 | src/main/java/com/campusnexus/dto/ProgressResponse.java | DTO | ✅ Created | Progress response |
| 91 | src/main/java/com/campusnexus/dto/DashboardResponse.java | DTO | ✅ Created | Dashboard stats |
| 92 | src/main/java/com/campusnexus/exception/ResourceNotFoundException.java | Exception | ✅ Created | 404 |
| 93 | src/main/java/com/campusnexus/exception/UnauthorizedException.java | Exception | ✅ Created | 403 |
| 94 | src/main/java/com/campusnexus/exception/BadRequestException.java | Exception | ✅ Created | 400 |
| 95 | src/main/java/com/campusnexus/exception/DuplicateResourceException.java | Exception | ✅ Created | 409 |
| 96 | src/main/java/com/campusnexus/exception/PaymentException.java | Exception | ✅ Created | 402 |
| 97 | src/main/java/com/campusnexus/exception/GlobalExceptionHandler.java | Exception | ✅ Created | Global handler |
| 98 | src/main/java/com/campusnexus/security/JwtUtil.java | Security | ✅ Created | JWT generation/validation |
| 99 | src/main/java/com/campusnexus/security/JwtAuthFilter.java | Security | ✅ Created | JWT filter with blocklist |
| 100 | src/main/java/com/campusnexus/security/UserDetailsServiceImpl.java | Security | ✅ Created | UserDetails from DB |
| 101 | src/main/java/com/campusnexus/security/JwtAuthEntryPoint.java | Security | ✅ Created | Auth entry point |
| 102 | src/main/java/com/campusnexus/config/SecurityConfig.java | Config | ✏️ Modified | Spring Security 7 config, CORS fix, secured upload paths, and public guest event permissions |
| 103 | src/main/java/com/campusnexus/config/SwaggerConfig.java | Config | ✏️ Modified | OpenAPI/Swagger config + ngrok servers |
| 104 | src/main/java/com/campusnexus/config/FirebaseConfig.java | Config | ✅ Created | Firebase init |
| 105 | src/main/java/com/campusnexus/config/StripeConfig.java | Config | ✏️ Modified | Stripe StripeClient bean |
| 149 | src/main/java/com/campusnexus/config/CorsConfig.java | Config | ✅ Created | Highest-priority CorsFilter bean |
| 106 | src/main/java/com/campusnexus/config/DataInitializer.java | Config | ✏️ Modified | Admin seeder & automated database constraints startup migrator |
| 107 | src/main/java/com/campusnexus/service/AuthService.java | Service | ✅ Created | Auth interface |
| 108 | src/main/java/com/campusnexus/service/CollegeService.java | Service | ✅ Created | College interface |
| 109 | src/main/java/com/campusnexus/service/DepartmentService.java | Service | ✅ Created | Department interface |
| 110 | src/main/java/com/campusnexus/service/EventService.java | Service | ✏️ Modified | Added deleteEvent(UUID eventId, UUID currentUserId) method signature |
| 111 | src/main/java/com/campusnexus/service/EventRegistrationService.java | Service | ✅ Created | Registration interface |
| 112 | src/main/java/com/campusnexus/service/ClubService.java | Service | ✏️ Modified | Added status filtering to getClubRequests for HOD/Principal |
| 113 | src/main/java/com/campusnexus/service/BatchService.java | Service | ✏️ Modified | Added getSectionsForStudent |
| 114 | src/main/java/com/campusnexus/service/SubmissionService.java | Service | ✅ Created | Submission interface |
| 115 | src/main/java/com/campusnexus/service/NotesService.java | Service | ✏️ Modified | Notes interface |
| 116 | src/main/java/com/campusnexus/service/BroadcastService.java | Service | ✅ Created | Broadcast interface |
| 117 | src/main/java/com/campusnexus/service/SeminarHallService.java | Service | ✅ Created | SeminarHall interface |
| 118 | src/main/java/com/campusnexus/service/TimetableService.java | Service | ✅ Created | Timetable interface |
| 119 | src/main/java/com/campusnexus/service/TeacherAvailabilityService.java | Service | ✅ Created | Availability interface |
| 120 | src/main/java/com/campusnexus/service/StudentProgressService.java | Service | ✅ Created | Progress interface |
| 121 | src/main/java/com/campusnexus/service/DashboardService.java | Service | ✅ Created | Dashboard interface |
| 122 | src/main/java/com/campusnexus/service/NotificationService.java | Service | ✅ Created | Notification interface |
| 123 | src/main/java/com/campusnexus/service/impl/AuthServiceImpl.java | ServiceImpl | ✅ Created | Auth with register/login/refresh/logout |
| 124 | src/main/java/com/campusnexus/service/impl/CollegeServiceImpl.java | ServiceImpl | ✅ Created | College CRUD with code gen |
| 125 | src/main/java/com/campusnexus/service/impl/DepartmentServiceImpl.java | ServiceImpl | ✅ Created | Department + HOD assignment |
| 126 | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | ServiceImpl | ✏️ Modified | Full rewrite: removed DB-write on GET (checkAndUpdateStatus replaced with EventStatusUtil.compute); added deleteEvent with cascade cleanup; price normalization; updateEventStatus restricted to CANCELLED only |
| 126.1 | src/main/java/com/campusnexus/service/impl/FirebaseStorageService.java | ServiceImpl | ✏️ Modified | Added deleteFile(String url) method for best-effort Firebase image removal on event delete |
| 126.2 | src/main/java/com/campusnexus/util/EventStatusUtil.java | Util | ✅ Created | Pure static utility: computes EventStatus from datetimes (UTC); never writes to DB; shared by EventServiceImpl, EventRegistrationServiceImpl, ExternalEventServiceImpl |
| 126.3 | normalize_ticket_prices.sql | Migration | ✅ Created | One-time SQL migration: normalizes all ticket_price < 0 to 0; wrapped in transaction with post-validation |
| 127 | src/main/java/com/campusnexus/service/impl/EventRegistrationServiceImpl.java | ServiceImpl | ✏️ Modified | Added EventStatusUtil status guard (COMPLETED/CANCELLED blocked) before duplicate/capacity checks |
| 128 | src/main/java/com/campusnexus/service/impl/ClubServiceImpl.java | ServiceImpl | ✏️ Modified | Implemented status filtering logic for club requests |
| 129 | src/main/java/com/campusnexus/service/impl/BatchServiceImpl.java | ServiceImpl | ✏️ Modified | Implemented filtering in getSectionsForStudent for pending tasks |
| 130 | src/main/java/com/campusnexus/service/impl/SubmissionServiceImpl.java | ServiceImpl | ✏️ Modified | Updated mapping logic to include teamMemberNames and teamSize |
| 131 | src/main/java/com/campusnexus/service/impl/NotesServiceImpl.java | ServiceImpl | ✏️ Modified | Notes CRUD with division and numeric yearLabel formatting |
| 132 | src/main/java/com/campusnexus/service/impl/BroadcastServiceImpl.java | ServiceImpl | ✏️ Modified | Handled senderRole and Campus Admin display |
| 133 | src/main/java/com/campusnexus/service/impl/SeminarHallServiceImpl.java | ServiceImpl | ✅ Created | Seminar hall CRUD |
| 134 | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | ServiceImpl | ✏️ Modified | Timetable CRUD with customized getTimetableForStudent method |
| 135 | src/main/java/com/campusnexus/service/impl/TeacherAvailabilityServiceImpl.java | ServiceImpl | ✅ Created | Availability CRUD |
| 136 | src/main/java/com/campusnexus/service/impl/StudentProgressServiceImpl.java | ServiceImpl | ✅ Created | Progress tracking |
| 137 | src/main/java/com/campusnexus/service/impl/DashboardServiceImpl.java | ServiceImpl | ✅ Created | Dashboard stats |
| 138 | src/main/java/com/campusnexus/service/impl/NotificationServiceImpl.java | ServiceImpl | ✅ Created | Email sending |
| 139 | src/main/java/com/campusnexus/service/impl/FirebaseStorageService.java | ServiceImpl | ✅ Created | Firebase file upload |
| 140 | src/main/java/com/campusnexus/service/impl/FCMService.java | ServiceImpl | ✅ Created | FCM push notifications |
| 141 | src/main/java/com/campusnexus/service/impl/StripeService.java | ServiceImpl | ✏️ Modified | Stripe v31 StripeClient + webhook with guest registration support |
| 142 | src/main/java/com/campusnexus/controller/AuthController.java | Controller | ✏️ Modified | Auth endpoints + @CrossOrigin |
| 143 | src/main/java/com/campusnexus/controller/CampusAdminController.java | Controller | ✏️ Modified | Admin endpoints + event participant/status, and guest external participant listing |
| 144 | src/main/java/com/campusnexus/controller/PrincipalController.java | Controller | ✏️ Modified | Supported status filter in club requests endpoint |
| 145 | src/main/java/com/campusnexus/controller/HODController.java | Controller | ✏️ Modified | Supported status filter in club requests endpoint |
| 146 | src/main/java/com/campusnexus/controller/ProfessorController.java | Controller | ✏️ Modified | Professor endpoints + event participant/status |
| 147 | src/main/java/com/campusnexus/controller/StudentController.java | Controller | ✏️ Modified | Modified uploads, GET /notes to filter by student profile, and GET /timetable to support profile fallback |
| 148 | src/main/java/com/campusnexus/controller/WebhookController.java | Controller | ✏️ Modified | Stripe webhook + @CrossOrigin |
| 148.1 | src/main/java/com/campusnexus/controller/EventController.java | Controller | ✏️ Modified | Added DELETE /api/events/{eventId} (owner-only); updated updateEventStatus Javadoc to reflect CANCELLED-only restriction |
| 149 | ../campus_connect_frontend/src/api/admin.api.js | API | ✏️ Modified | Admin API client |
| 150 | ../campus_connect_frontend/src/api/professor.api.js | API | ✏️ Modified | Professor API client |
| 150 | ../campus_connect_fronted/src/pages/student/EventsPage.jsx | Page | ✏️ Modified | Refactored to use isRegistered and optimistic updates |
| 152 | API_ENDPOINTS.md | Documentation | ✏️ Modified | Updated club request filtering documentation |
| 153 | generate_api.py | Script | ✅ Created | Python script to extract endpoints |
| 154 | api_dump.txt | Data | ✅ Created | Extracted raw DTOs and endpoints |
| 155 | generate_md.py | Script | ✅ Created | Python script to format Markdown |
| 156 | src/main/java/com/campusnexus/converter/StringListConverter.java | Converter | ✅ Created | JSON to List converter for profile skills/interests |
| 157 | src/main/java/com/campusnexus/entity/StudentProfile.java | Entity | ✅ Created | Student profile details entity |
| 158 | src/main/java/com/campusnexus/repository/StudentProfileRepository.java | Repository | ✅ Created | Student profile queries |
| 159 | src/main/java/com/campusnexus/dto/UpdateStudentProfileRequest.java | DTO | ✅ Created | Extended profile update request |
| 160 | src/main/java/com/campusnexus/dto/StudentProfileResponse.java | DTO | ✏️ Modified | Added updatedAt field to DTO |
| 161 | src/main/java/com/campusnexus/service/StudentProfileService.java | Service | ✅ Created | Profile logic interface |
| 162 | src/main/java/com/campusnexus/service/impl/StudentProfileServiceImpl.java | ServiceImpl | ✏️ Modified | Mapped updatedAt field |
| 163 | src/main/java/com/campusnexus/enums/TimetableStatus.java | Enum | ✅ Created | DRAFT, PUBLISHED, ARCHIVED, DELETED |
| 164 | src/main/java/com/campusnexus/dto/AISuggestRequest.java | DTO | ✅ Created | AI timetable suggestion request with teacher-subject mappings |
| 165 | src/main/java/com/campusnexus/config/RestTemplateConfig.java | Config | ✅ Created | RestTemplate bean for Groq API calls |
| 33 | src/main/java/com/campusnexus/entity/Timetable.java | Entity | ✏️ Modified | Added year, semester, division, status, teacherName; mapped startTime/endTime to from_time/to_time columns |
| 71 | src/main/java/com/campusnexus/dto/TimetableRequest.java | DTO | ✏️ Modified | Removed obsolete @NotNull validation on departmentId field |
| 88 | src/main/java/com/campusnexus/dto/TimetableResponse.java | DTO | ✏️ Modified | Added departmentId, teacherId, year, semester, division, status, hasConflict, conflictReason, type |
| 50 | src/main/java/com/campusnexus/repository/TimetableRepository.java | Repository | ✏️ Modified | Added status-filtered query methods and clash check by status |
| 49 | src/main/java/com/campusnexus/repository/TeacherAvailabilityRepository.java | Repository | ✏️ Modified | Added findByTeacher_IdAndStatusIn query |
| 118 | src/main/java/com/campusnexus/service/TimetableService.java | Service | ✏️ Modified | Updated updateTimetable signature to accept departmentId |
| 134 | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | ServiceImpl | ✏️ Modified | Added department ownership check to updateTimetable method |
| 145 | src/main/java/com/campusnexus/controller/HODController.java | Controller | ✏️ Modified | Updated updateTimetable endpoint to retrieve and validate departmentId server-side |
| 147 | src/main/java/com/campusnexus/controller/StudentController.java | Controller | ✏️ Modified | Timetable endpoint now requires year, semester, division params |
| 146 | src/main/java/com/campusnexus/controller/ProfessorController.java | Controller | ✏️ Modified | Added GET /timetable and GET /timetable/merged endpoints |
| 2 | src/main/resources/application.properties | Config | ✏️ Modified | Added Groq AI config properties |
| 166 | ../campus_connect_fronted/src/api/student.api.js | API | ✏️ Modified | Added profile picture and resume upload APIs |
| 167 | ../campus_connect_fronted/src/pages/student/ProfilePage.jsx | Page | ✏️ Modified | Fixed profile prefill, form reset, and file upload submit |
| 168 | src/main/java/com/campusnexus/config/CloudinaryConfig.java | Config | ✅ Created | Configuration for Cloudinary client bean |
| 169 | src/main/java/com/campusnexus/service/CloudinaryService.java | Service | ✅ Created | Cloudinary media upload service interface |
| 170 | src/main/java/com/campusnexus/service/impl/CloudinaryServiceImpl.java | ServiceImpl | ✏️ Modified | Cloudinary media upload service implementation |
| 171 | src/main/java/com/campusnexus/controller/UploadController.java | Controller | ✏️ Modified | Generic file upload controller |
| 172 | ../campus_connect_fronted/src/utils/constants.js | Config | ✏️ Modified | Updated YEAR_LABELS mapping to return numeric format |
| 173 | ../campus_connect_fronted/src/pages/professor/NotesPage.jsx | Page | ✏️ Modified | Updated year selection dropdown option labels to Year 1-4 |
| 174 | src/main/java/com/campusnexus/entity/ExternalRegistration.java | Entity | ✅ Created | Guest registration details entity |
| 175 | src/main/java/com/campusnexus/repository/ExternalRegistrationRepository.java | Repository | ✅ Created | Repository for querying guest registrations |
| 176 | src/main/java/com/campusnexus/dto/ExternalRegistrationRequest.java | DTO | ✅ Created | Request DTO for guest registrations |
| 177 | src/main/java/com/campusnexus/dto/ExternalRegistrationResponse.java | DTO | ✅ Created | Response DTO for guest registrations |
| 178 | src/main/java/com/campusnexus/dto/ExternalEventResponse.java | DTO | ✅ Created | Simplified response DTO for guest events |
| 179 | src/main/java/com/campusnexus/service/ExternalEventService.java | Service | ✅ Created | Service interface for guest events |
| 180 | src/main/java/com/campusnexus/service/impl/ExternalEventServiceImpl.java | ServiceImpl | ✏️ Modified | Added EventStatusUtil status guard (COMPLETED/CANCELLED blocked) before duplicate email check |
| 181 | src/main/java/com/campusnexus/controller/ExternalEventController.java | Controller | ✅ Created | Public controller for guest event registrations |
| 182 | ../campus_connect_fronted/src/pages/professor/TimetablePage.jsx | Component | ✏️ Modified | Removed Merged Schedule tab, query, and UI components from Professor module |
| 183 | ../campus_connect_fronted/src/components/layout/DashboardLayout.jsx | Component | ✏️ Modified | Removed search bar and notification bell icon from header layout |
| 184 | ../campus_connect_fronted/src/pages/student/TeacherAvailabilityPage.jsx | Component | ✏️ Modified | Removed non-functional Consult Online and Inquire via Chat buttons |

---

## Entity Registry

| Entity | Table Name | Primary Key | Relationships | Status |
|--------|-----------|-------------|---------------|--------|
| User | users | UUID | ManyToOne: College, Department; @EqualsAndHashCode(id only) | ✅ |
| College | colleges | UUID | ManyToOne: principal(User), createdBy(User); @EqualsAndHashCode(id only) | ✅ |
| Department | departments | UUID | ManyToOne: College, hod(User); @EqualsAndHashCode(id only) | ✅ |
| Batch | batches | UUID | ManyToOne: Department, professor(User) | ✅ |
| BatchSection | batch_sections | UUID | ManyToOne: Batch | ✅ |
| Submission | submissions | UUID | ManyToOne: BatchSection, student(User), reviewedBy(User); ManyToMany: teamMembers | ✅ |
| Club | clubs | UUID | ManyToOne: College, @EqualsAndHashCode(id only) | ✅ |
| Event | events | UUID | ManyToOne: College, Department, Club, createdBy, parentEvent; OneToMany: registrations (@JsonIgnore) | ✅ |
| EventRegistration | event_registrations | UUID | ManyToOne: Event (@JsonIgnore), student(User) | ✅ |
| SeminarHall | seminar_halls | UUID | ManyToOne: College, Department | ✅ |
| SeminarHallBooking | seminar_hall_bookings | UUID | ManyToOne: SeminarHall, bookedBy(User) | ✅ |
| Broadcast | broadcasts | UUID | ManyToOne: sentBy(User), College, Department | ✅ |
| Notes | notes | UUID | ManyToOne: uploadedBy(User), Department; Fields: year(nullable), semester(nullable), division | ✏️ Modified |
| TeacherAvailability | teacher_availability | UUID | ManyToOne: teacher(User) | ✅ |
| Timetable | timetables | UUID | ManyToOne: Department, Batch(nullable), teacher(User); Fields: year, semester, division, startTime, endTime, subject, status(TimetableStatus), teacherName | ✏️ Modified |
| StudentProgress | student_progress | UUID | ManyToOne: student(User), updatedBy(User) | ✅ |
| InvalidatedToken | invalidated_tokens | UUID | None | ✅ |
| StudentProfile | student_profiles | UUID | OneToOne: User | ✅ |
| ExternalRegistration | external_registrations | UUID | ManyToOne: Event (@JsonIgnore) | ✅ |

---

## API Endpoint Registry

| Method | Endpoint | Controller | Role Required | Description | Status |
|--------|----------|-----------|---------------|-------------|--------|
| POST | /api/auth/register | AuthController | Public | Register user | ✅ |
| POST | /api/auth/login | AuthController | Public | Login | ✅ |
| POST | /api/auth/refresh | AuthController | Public | Refresh token | ✅ |
| POST | /api/auth/logout | AuthController | Public | Logout | ✅ |
| GET | /api/events/{eventId}/participants | EventController | All | List event participants | ✅ |
| PUT | /api/events/{eventId}/status | EventController | All (owner) | Cancel event (CANCELLED only) — time-based statuses are computed | ✏️ Modified |
| DELETE | /api/events/{eventId} | EventController | All (owner) | Permanently delete event + cascade registrations/sub-events/image | ✅ Created |
| POST | /api/admin/colleges | CampusAdminController | CAMPUS_ADMIN | Create college | ✅ |
| GET | /api/admin/colleges | CampusAdminController | CAMPUS_ADMIN | List colleges | ✅ |
| PUT | /api/admin/colleges/{id}/approve | CampusAdminController | CAMPUS_ADMIN | Approve college | ✅ |
| PUT | /api/admin/colleges/{id}/status | CampusAdminController | CAMPUS_ADMIN | Update status | ✅ |
| PUT | /api/admin/colleges/{id}/assign-principal | CampusAdminController | CAMPUS_ADMIN | Assign principal | ✅ |
| POST | /api/admin/seminar-halls | CampusAdminController | CAMPUS_ADMIN | Create public hall | ✅ |
| GET | /api/admin/seminar-halls | CampusAdminController | CAMPUS_ADMIN | List public halls | ✅ |
| POST | /api/admin/events | CampusAdminController | CAMPUS_ADMIN | Create campus event | ✅ |
| GET | /api/admin/events | CampusAdminController | CAMPUS_ADMIN | List campus events | ✅ |
| POST | /api/admin/broadcasts | CampusAdminController | CAMPUS_ADMIN | Broadcast campus msg | ✅ |
| GET | /api/admin/dashboard | CampusAdminController | CAMPUS_ADMIN | Dashboard stats | ✅ |
| GET | /api/admin/events/{eventId}/participants | CampusAdminController | CAMPUS_ADMIN | List event participants | ✅ |
| GET | /api/admin/events/{eventId}/external-participants | CampusAdminController | CAMPUS_ADMIN | List external event participants | ✅ Created |
| PUT | /api/admin/events/{eventId}/status | CampusAdminController | CAMPUS_ADMIN | Update event status | ✅ |
| POST | /api/principal/departments | PrincipalController | PRINCIPAL | Create department | ✅ |
| GET | /api/principal/departments | PrincipalController | PRINCIPAL | List departments | ✅ |
| PUT | /api/principal/departments/{id}/assign-hod | PrincipalController | PRINCIPAL | Assign HOD | ✅ |
| GET | /api/principal/professors | PrincipalController | PRINCIPAL | List professors | ✅ |
| POST | /api/principal/club-requests/{id}/approve | PrincipalController | PRINCIPAL | Approve club | ✅ |
| POST | /api/principal/club-requests/{id}/reject | PrincipalController | PRINCIPAL | Reject club | ✅ |
| GET | /api/principal/club-requests | PrincipalController | PRINCIPAL | List club requests with status filter | ✅ |
| POST | /api/principal/seminar-halls | PrincipalController | PRINCIPAL | Create college hall | ✅ |
| GET | /api/principal/seminar-halls | PrincipalController | PRINCIPAL | List college halls | ✅ |
| PUT | /api/principal/seminar-halls/{id} | PrincipalController | PRINCIPAL | Update hall | ✅ |
| POST | /api/principal/events | PrincipalController | PRINCIPAL | Create college event | ✅ |
| GET | /api/principal/events | PrincipalController | PRINCIPAL | List college events | ✅ |
| PUT | /api/principal/events/{id}/approve | PrincipalController | PRINCIPAL | Approve event | ✅ |
| GET | /api/principal/events/{eventId}/participants | PrincipalController | PRINCIPAL | List event participants | ✅ |
| PUT | /api/principal/events/{eventId}/status | PrincipalController | PRINCIPAL | Update event status | ✅ |
| POST | /api/principal/broadcasts | PrincipalController | PRINCIPAL | Broadcast college msg | ✅ |
| GET | /api/principal/broadcasts | PrincipalController | PRINCIPAL | List own broadcasts | ✅ |
| POST | /api/hod/events | HODController | HOD | Create dept event | ✅ |
| GET | /api/hod/events | HODController | HOD | List dept events | ✅ |
| PUT | /api/hod/events/{id}/approve | HODController | HOD | Approve event | ✅ |
| GET | /api/hod/events/{eventId}/participants | HODController | HOD | List event participants | ✅ |
| PUT | /api/hod/events/{eventId}/status | HODController | HOD | Update event status | ✅ |
| POST | /api/hod/club-requests/{id}/approve | HODController | HOD | Approve club (HOD) | ✅ |
| POST | /api/hod/club-requests/{id}/reject | HODController | HOD | Reject club (HOD) | ✅ |
| GET | /api/hod/club-requests | HODController | HOD | List club requests with status filter | ✅ |
| POST | /api/hod/timetable | HODController | HOD | Create timetable | ✅ |
| GET | /api/hod/timetable | HODController | HOD | List PUBLISHED+DRAFT timetable | ✏️ Modified |
| PUT | /api/hod/timetable/{id} | HODController | HOD | Update timetable | ✅ |
| DELETE | /api/hod/timetable/{id} | HODController | HOD | Soft delete timetable (DELETED status) | ✏️ Modified |
| POST | /api/hod/timetable/ai-suggest | HODController | HOD | AI timetable suggestion via Groq | ✅ Created |
| POST | /api/hod/timetable/publish | HODController | HOD | Publish timetable with clash check | ✅ Created |
| PUT | /api/hod/timetable/archive | HODController | HOD | Archive semester timetable | ✅ Created |
| GET | /api/hod/timetable/archived | HODController | HOD | List archived timetable | ✅ Created |
| GET | /api/hod/professors | HODController | HOD | List professors in college | ✅ |
| POST | /api/hod/seminar-halls | HODController | HOD | Create dept hall | ✅ |
| GET | /api/hod/seminar-halls | HODController | HOD | List dept halls | ✅ |
| POST | /api/hod/broadcasts | HODController | HOD | Broadcast dept msg | ✅ |
| POST | /api/professor/batches | ProfessorController | PROFESSOR | Create batch (auto-assign dept) | ✏️ Modified |
| GET | /api/professor/batches | ProfessorController | PROFESSOR | List own batches | ✅ |
| POST | /api/professor/batches/{id}/sections | ProfessorController | PROFESSOR | Create section | ✅ |
| GET | /api/professor/batches/{id}/sections | ProfessorController | PROFESSOR | List sections | ✅ |
| PUT | /api/professor/batches/{batchId} | ProfessorController | PROFESSOR | Update batch | ✅ |
| DELETE | /api/professor/batches/{batchId} | ProfessorController | PROFESSOR | Delete batch | ✅ |
| PUT | /api/professor/sections/{sectionId} | ProfessorController | PROFESSOR | Update section | ✅ |
| DELETE | /api/professor/sections/{sectionId} | ProfessorController | PROFESSOR | Delete section | ✅ |
| GET | /api/professor/submissions/{sectionId} | ProfessorController | PROFESSOR | View submissions | ✅ |
| PUT | /api/professor/submissions/{id}/remark | ProfessorController | PROFESSOR | Give remark | ✅ |
| POST | /api/professor/notes | ProfessorController | PROFESSOR | Upload notes | ✅ |
| GET | /api/professor/notes | ProfessorController | PROFESSOR | List own notes | ✅ |
| PUT | /api/professor/notes/{id} | ProfessorController | PROFESSOR | Update note | ✅ |
| DELETE | /api/professor/notes/{id} | ProfessorController | PROFESSOR | Delete note | ✅ |
| POST | /api/professor/availability | ProfessorController | PROFESSOR | Mark availability | ✅ |
| GET | /api/professor/availability | ProfessorController | PROFESSOR | View availability | ✅ |
| PUT | /api/professor/availability/{id} | ProfessorController | PROFESSOR | Update availability | ✅ |
| DELETE | /api/professor/availability/{id} | ProfessorController | PROFESSOR | Delete availability | ✅ |
| POST | /api/professor/events | ProfessorController | PROFESSOR | Create event | ✅ |
| GET | /api/professor/events | ProfessorController | PROFESSOR | List own events | ✅ |
| GET | /api/professor/events/{eventId}/participants | ProfessorController | PROFESSOR | List event participants | ✅ |
| PUT | /api/professor/events/{eventId}/status | ProfessorController | PROFESSOR | Update event status | ✅ |
| POST | /api/professor/student-progress/{id} | ProfessorController | PROFESSOR | Update progress | ✅ |
| GET | /api/professor/student-progress/{id} | ProfessorController | PROFESSOR | View progress | ✅ |
| GET | /api/student/events | StudentController | STUDENT | List visible events | ✅ |
| POST | /api/student/events/{id}/register | StudentController | STUDENT | Register for event | ✅ |
| GET | /api/student/events/my-registrations | StudentController | STUDENT | My registrations | ✅ |
| GET | /api/student/events/{id}/ticket | StudentController | STUDENT | Ticket details | ✅ |
| POST | /api/student/clubs | StudentController | STUDENT | Request club | ✅ |
| GET | /api/student/clubs | StudentController | STUDENT | List clubs | ✅ |
| POST | /api/student/clubs/{id}/join | StudentController | STUDENT | Join club | ✅ |
| POST | /api/student/submissions/{sectionId} | StudentController | STUDENT | Submit work | ✅ |
| GET | /api/student/submissions | StudentController | STUDENT | My submissions | ✅ |
| GET | /api/student/notes | StudentController | STUDENT | Browse notes | ✅ |
| GET | /api/student/broadcasts | StudentController | STUDENT | View broadcasts | ✅ |
| GET | /api/student/timetable | StudentController | STUDENT | View timetable (optional parameters with profile fallback) | ✏️ Modified |
| GET | /api/student/teacher-availability | StudentController | STUDENT | View availability | ✅ |
| GET | /api/student/progress | StudentController | STUDENT | View own progress | ✅ |
| GET | /api/student/sections | StudentController | STUDENT | List relevant sections | ✅ |
| GET | /api/student/profile | StudentController | STUDENT | Get detailed profile | ✅ |
| PUT | /api/student/profile | StudentController | STUDENT | Update detailed profile | ✏️ Modified |
| POST | /api/student/upload/profile-pic | StudentController | STUDENT | Upload profile picture | ✅ Created |
| POST | /api/student/upload/resume | StudentController | STUDENT | Upload resume | ✅ Created |
| GET | /api/professor/timetable | ProfessorController | PROFESSOR | View own teaching schedule | ✅ Created |
| GET | /api/professor/timetable/merged | ProfessorController | PROFESSOR | View merged teaching + availability | ✅ Created |
| POST | /api/webhook/stripe | WebhookController | Public | Stripe webhook | ✅ |
| POST | /api/upload/image | UploadController | Authenticated | Upload general image | ✅ Created |
| POST | /api/upload/profile-pic | UploadController | Authenticated | Upload profile picture | ✅ Created |
| POST | /api/upload/document | UploadController | Authenticated | Upload document | ✅ Created |
| POST | /api/upload/event-image | UploadController | Authenticated | Upload event poster | ✅ Created |
| POST | /api/upload/notes | UploadController | Authenticated | Upload notes file | ✅ Created |
| POST | /api/upload/submission | UploadController | Authenticated | Upload submission file | ✅ Created |
| GET | /api/external/events | ExternalEventController | Public | Get open external events | ✅ Created |
| POST | /api/external/events/{eventId}/register | ExternalEventController | Public | Register external guest | ✅ Created |
| GET | /api/external/registrations | ExternalEventController | Public | Get guest registrations by email | ✅ Created |

---

## Enum Registry

| Enum | Values | Status |
|------|--------|--------|
| Role | CAMPUS_ADMIN, PRINCIPAL, HOD, PROFESSOR, STUDENT | ✅ |
| CollegeStatus | PENDING, ACTIVE, INACTIVE | ✅ |
| EventStatus | UPCOMING, ONGOING, COMPLETED, CANCELLED | ✅ |
| EventLevel | CAMPUS, COLLEGE, DEPARTMENT, CLUB | ✅ |
| EventType | MAIN, SUB | ✅ |
| ClubStatus | PENDING_HOD, PENDING_PRINCIPAL, APPROVED, REJECTED | ✅ |
| BatchSectionType | PROJECT, SEMINAR, INTERNSHIP | ✅ |
| SubmissionType | INDIVIDUAL, TEAM | ✅ |
| SubmissionStatus | SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED | ✅ |
| SeminarHallType | PUBLIC, PRIVATE | ✅ |
| SeminarHallStatus | AVAILABLE, BOOKED, MAINTENANCE | ✅ |
| BroadcastLevel | CAMPUS, COLLEGE, DEPARTMENT | ✅ |
| TicketStatus | PENDING, CONFIRMED, CANCELLED | ✅ |
| PaymentStatus | PENDING, SUCCESS, FAILED | ✅ |
| TeacherAvailabilityStatus | AVAILABLE, BUSY, ON_LEAVE | ✅ |
| TimetableStatus | DRAFT, PUBLISHED, ARCHIVED, DELETED | ✅ |

---

## Service Registry

| Service Interface | Implementation Class | Methods | Status |
|------------------|---------------------|---------|--------|
| AuthService | AuthServiceImpl | register, login, refreshToken, logout | ✅ |
| CollegeService | CollegeServiceImpl | createCollege, getAllColleges, approveCollege, updateCollegeStatus, assignPrincipal | ✅ |
| DepartmentService | DepartmentServiceImpl | createDepartment, getDepartmentsByCollege, assignHOD | ✅ |
| EventService | EventServiceImpl | createEvent, getEventsByLevel/College/Department/Creator, getVisibleEventsForStudent, approveEvent, updateEventStatus (CANCELLED only), **deleteEvent** | ✏️ Modified |
| EventRegistrationService | EventRegistrationServiceImpl | registerForEvent, getStudentRegistrations, getTicketDetails, getParticipants | ✅ |
| ClubService | ClubServiceImpl | createClub, getClubsByCollege, approveByHOD/Principal, rejectByHOD/Principal, getClubRequests, joinClub | ✅ |
| BatchService | BatchServiceImpl | createBatch, getBatchesByProfessor, createSection, getSectionsByBatch, getSectionsForStudent, updateBatch, deleteBatch, updateSection, deleteSection | ✅ |
| SubmissionService | SubmissionServiceImpl | createSubmission (validated), getSubmissionsBySection, getSubmissionsByStudent (team-aware), addRemark | ✅ |
| NotesService | NotesServiceImpl | uploadNotes, getNotesByProfessor/Department, updateNotes, deleteNotes, getNotesByYearSemesterDivision | ✏️ Modified |
| BroadcastService | BroadcastServiceImpl | createBroadcast, getBroadcastsBySender, getRelevantBroadcasts | ✅ |
| SeminarHallService | SeminarHallServiceImpl | createSeminarHall, getPublic/ByCollege/ByDepartment, updateSeminarHall | ✅ |
| TimetableService | TimetableServiceImpl | createTimetable, getTimetableByDepartment, updateTimetable, deleteTimetable, generateAISuggestion, publishTimetable, archiveSemester, deleteTimetableSlot, getTimetableForStudent, getTimetableForProfessor, getTeacherScheduleMerged, getTimetableByDepartmentAndStatuses, getArchivedByDepartment | ✏️ Modified |
| TeacherAvailabilityService | TeacherAvailabilityServiceImpl | createAvailability, getByTeacher/Department, updateAvailability, deleteAvailability | ✅ |
| StudentProgressService | StudentProgressServiceImpl | updateProgress, getProgressByStudent | ✅ |
| DashboardService | DashboardServiceImpl | getDashboardStats | ✅ |
| NotificationService | NotificationServiceImpl | sendEmail (async) | ✅ |
| — | FirebaseStorageService | uploadFile, **deleteFile** | ✏️ Modified |
| — | FCMService | sendToTopic, sendToToken | ✅ |
| — | StripeService | createPaymentIntent, handleWebhookEvent | ✅ |
| StudentProfileService | StudentProfileServiceImpl | getStudentProfile, updateStudentProfile | ✅ |
| CloudinaryService | CloudinaryServiceImpl | uploadFile, deleteFile | ✅ |
| ExternalEventService | ExternalEventServiceImpl | getOpenExternalEvents, registerGuest, getMyRegistrations, getRegistrationsByEvent | ✅ |

---

## Dependencies Used

| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-web | 4.0.3 | REST API, embedded Tomcat |
| spring-boot-starter-data-jpa | 4.0.3 | JPA/Hibernate |
| spring-boot-starter-security | 4.0.3 | Spring Security 7 |
| spring-boot-starter-validation | 4.0.3 | Bean validation |
| spring-boot-starter-mail | 4.0.3 | Email sending |
| postgresql | runtime | PostgreSQL JDBC |
| jjwt-api / impl / jackson | 0.12.6 | JWT tokens |
| lombok | 1.18.36 | Boilerplate reduction |
| mapstruct | 1.6.3 | Object mapping |
| springdoc-openapi-starter-webmvc-ui | 2.8.6 | Swagger UI |
| firebase-admin | 9.4.2 | Firebase Storage + FCM |
| stripe-java | 31.1.0 | Stripe payments (StripeClient API) |
| cloudinary-http44 | 1.39.0 | Cloudinary media upload API |

---

## Environment Variables / Properties

| Property Key | Default Value | Description |
|-------------|---------------|-------------|
| spring.datasource.url | jdbc:postgresql://localhost:5432/campus_nexus | PostgreSQL URL |
| spring.datasource.username | postgres | DB username |
| spring.datasource.password | postgres | DB password |
| spring.jpa.hibernate.ddl-auto | update | Schema auto-update |
| spring.jpa.show-sql | true | Show SQL in logs |
| jwt.secret | CampusNexusSuperSecretKey... | JWT signing key |
| jwt.access-token-expiry | 86400000 | 24 hours |
| jwt.refresh-token-expiry | 604800000 | 7 days |
| firebase.service-account-path | classpath:firebase-service-account.json | Firebase key |
| firebase.storage-bucket | your-project.appspot.com | Storage bucket |
| stripe.api-key | sk_test_xxx | Stripe key |
| stripe.webhook-secret | whsec_xxx | Webhook secret |
| spring.mail.host | smtp.gmail.com | SMTP host |
| spring.mail.port | 587 | SMTP port |
| server.port | 8080 | App port |
| groq.api.key | gsk_your_key_here | Groq API key for AI timetable |
| groq.api.url | https://api.groq.com/openai/v1/chat/completions | Groq API endpoint |
| groq.model | llama-3.3-70b-versatile | Groq LLM model |
| cloudinary.cloud-name | YOUR_CLOUD_NAME | Cloudinary cloud name |
| cloudinary.api-key | YOUR_API_KEY | Cloudinary API key |
| cloudinary.api-secret | YOUR_API_SECRET | Cloudinary API secret |

---

## Change Log

| Timestamp | Action | File | Details |
|-----------|--------|------|---------|
| Step-222 | MODIFY | src/main/java/com/campusnexus/service/impl/ExternalEventServiceImpl.java | Added EventStatusUtil status guard: COMPLETED and CANCELLED events reject new external registrations |
| Step-221 | MODIFY | src/main/java/com/campusnexus/service/impl/EventRegistrationServiceImpl.java | Added EventStatusUtil status guard: COMPLETED and CANCELLED events reject new student registrations |
| Step-220 | MODIFY | src/main/java/com/campusnexus/controller/EventController.java | Added DELETE /api/events/{eventId} endpoint; updated updateEventStatus description to reflect CANCELLED-only policy |
| Step-219 | MODIFY | src/main/java/com/campusnexus/service/impl/FirebaseStorageService.java | Added deleteFile(String url) method for Firebase Storage blob deletion by URL |
| Step-218 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Full rewrite: replaced checkAndUpdateStatus DB-write with pure EventStatusUtil.compute(); restricted updateEventStatus to CANCELLED; added deleteEvent with cascade; normalized negative ticketPrice in mapToResponse and createEvent |
| Step-217 | MODIFY | src/main/java/com/campusnexus/service/EventService.java | Added deleteEvent(UUID eventId, UUID currentUserId) method signature with Javadoc |
| Step-216 | CREATE | src/main/java/com/campusnexus/util/EventStatusUtil.java | New shared static utility: computes EventStatus from event datetimes using UTC; never touches DB |
| Step-215 | MODIFY | src/main/java/com/campusnexus/dto/EventCreateRequest.java | Added @DecimalMin("0.00") and @Digits validation on ticketPrice field |
| Step-214 | MODIFY | normalize_ticket_prices.sql | Created one-time SQL migration to normalize legacy ticket_price = -1 records to 0 |
| Step-213 | MODIFY | ../campus_connect_fronted/src/pages/student/TeacherAvailabilityPage.jsx | Removed non-functional Consult Online and Inquire via Chat buttons |
| Step-212 | MODIFY | ../campus_connect_fronted/src/components/layout/DashboardLayout.jsx | Removed search bar and notification bell icon from header layout |
| Step-211 | MODIFY | ../campus_connect_frontend/src/api/professor.api.js | Removed unused getMergedSchedule API call |
| Step-210 | MODIFY | ../campus_connect_fronted/src/pages/professor/TimetablePage.jsx | Removed Merged Schedule tab, query, and UI components from Professor module |
| Step-209 | MODIFY | src/test/java/com/project/campus_connect_backend/CampusConnectBackendApplicationTests.java | Explicitly configured SpringBootTest classes parameter to fix context loading issue |
| Step-208 | MODIFY | src/main/java/com/campusnexus/controller/HODController.java | Updated updateTimetable endpoint to retrieve and validate HOD department ID server-side |
| Step-207 | MODIFY | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | Added department ownership verification logic to updateTimetable method |
| Step-206 | MODIFY | src/main/java/com/campusnexus/service/TimetableService.java | Updated updateTimetable signature to accept HOD's department ID |
| Step-205 | MODIFY | src/main/java/com/campusnexus/dto/TimetableRequest.java | Removed obsolete @NotNull validation from departmentId field |
| Step-204 | MODIFY | src/main/java/com/campusnexus/entity/Event.java | Changed openToExternal column definition to nullable=true to resolve database schema alteration failures |
| Step-203 | MODIFY | src/main/java/com/campusnexus/service/impl/StripeService.java | Resolved duplicate Event class import conflict |
| Step-202 | MODIFY | pom.xml | Removed maven-compiler-plugin configuration to let Spring Boot parent handle Lombok |
| Step-201 | MODIFY | pom.xml | Removed unused mapstruct-processor from annotation processor paths |
| Step-200 | MODIFY | src/main/java/com/campusnexus/config/SecurityConfig.java | Permitted external guest endpoints without authentication |
| Step-199 | MODIFY | src/main/java/com/campusnexus/controller/CampusAdminController.java | Added GET /api/admin/events/{eventId}/external-participants endpoint |
| Step-198 | CREATE | src/main/java/com/campusnexus/controller/ExternalEventController.java | Created ExternalEventController for guest endpoints |
| Step-197 | MODIFY | src/main/java/com/campusnexus/service/impl/StripeService.java | Supported guest registrations in createPaymentIntent and webhook handler |
| Step-196 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Set and mapped openToExternal field for events |
| Step-195 | CREATE | src/main/java/com/campusnexus/service/impl/ExternalEventServiceImpl.java | Created ExternalEventServiceImpl class |
| Step-194 | MODIFY | src/main/java/com/campusnexus/repository/EventRepository.java | Added findByOpenToExternalTrueAndStatusInOrderByStartDateTimeDesc for guest registrations |
| Step-193 | CREATE | src/main/java/com/campusnexus/service/ExternalEventService.java | Created ExternalEventService interface |
| Step-192 | MODIFY | src/main/java/com/campusnexus/dto/EventResponse.java | Added openToExternal field to event response DTO |
| Step-191 | MODIFY | src/main/java/com/campusnexus/dto/EventCreateRequest.java | Added openToExternal field to event creation request |
| Step-190 | CREATE | src/main/java/com/campusnexus/dto/ExternalEventResponse.java | Created ExternalEventResponse DTO |
| Step-189 | CREATE | src/main/java/com/campusnexus/dto/ExternalRegistrationResponse.java | Created ExternalRegistrationResponse DTO |
| Step-188 | CREATE | src/main/java/com/campusnexus/dto/ExternalRegistrationRequest.java | Created ExternalRegistrationRequest DTO |
| Step-187 | CREATE | src/main/java/com/campusnexus/repository/ExternalRegistrationRepository.java | Created ExternalRegistrationRepository interface |
| Step-186 | CREATE | src/main/java/com/campusnexus/entity/ExternalRegistration.java | Created ExternalRegistration entity for guest registrations |
| Step-185 | MODIFY | src/main/java/com/campusnexus/entity/Event.java | Added openToExternal field for guest registrations |
| Step-184 | MODIFY | ../campus_connect_fronted/src/pages/hod/TimetablePage.jsx | Added departmentId from useAuthStore to manual add slot request payload and replaced form tag with div block |
| Step-183 | MODIFY | ../campus_connect_fronted/src/pages/professor/NotesPage.jsx | Updated year selection dropdown option labels to Year 1-4 |
| Step-182 | MODIFY | ../campus_connect_fronted/src/utils/constants.js | Updated YEAR_LABELS mapping to return numeric format |
| Step-181 | MODIFY | src/main/java/com/campusnexus/config/DataInitializer.java | Added automated database startup migration to convert existing FE/SE/TE/BE timetables to 1/2/3/4 format |
| Step-180 | MODIFY | src/main/java/com/campusnexus/service/impl/NotesServiceImpl.java | Updated mapToResponse to return numeric string yearLabel instead of FE/SE/TE/BE codes |
| Step-179 | MODIFY | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | Updated normalizeYear to return numeric strings '1' through '4' instead of FE/SE/TE/BE codes |
| Step-178 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Made GET /timetable request parameters optional, adding student profile auto-fetching fallback |
| Step-177 | MODIFY | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | Added slf4j logger and refactored getTimetableForStudent to call new repository query |
| Step-176 | MODIFY | src/main/java/com/campusnexus/repository/TimetableRepository.java | Added findPublishedForStudent custom query for student timetable checks |
| Step-175 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Updated GET /notes to filter by authenticated student's profile year/semester/division |
| Step-174 | MODIFY | src/main/java/com/campusnexus/service/impl/NotesServiceImpl.java | Implemented getNotesByYearSemesterDivision, division mapping, and yearLabel formatting |
| Step-173 | MODIFY | src/main/java/com/campusnexus/service/NotesService.java | Added getNotesByYearSemesterDivision signature to interface |
| Step-172 | MODIFY | src/main/java/com/campusnexus/repository/NotesRepository.java | Added findRelevantNotes query with division filters |
| Step-171 | MODIFY | src/main/java/com/campusnexus/dto/NotesResponse.java | Added division and yearLabel fields to DTO |
| Step-170 | MODIFY | src/main/java/com/campusnexus/dto/NotesUploadRequest.java | Added optional division field to DTO |
| Step-169 | MODIFY | src/main/java/com/campusnexus/entity/Notes.java | Made year/semester nullable and added nullable division string |
| Step-168 | MODIFY | src/main/java/com/campusnexus/controller/UploadController.java | Added general profile-pic endpoint, configured dynamic submission type detection |
| Step-167 | MODIFY | src/main/java/com/campusnexus/service/impl/CloudinaryServiceImpl.java | Preserved raw upload file extensions in options map |
| Step-166 | MODIFY | PROJECT_CONTEXT.md | Updated final project statistics and summary registry |
| Step-165 | MODIFY | src/main/java/com/campusnexus/config/SecurityConfig.java | Secured /api/upload/** paths under Spring Security authorization filter |
| Step-164 | CREATE | src/main/java/com/campusnexus/controller/UploadController.java | Generic media upload endpoints supporting image, raw, and auto types |
| Step-163 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Replaced FirebaseStorageService with CloudinaryService for profile pic and resume uploads, persisting URLs to DB |
| Step-162 | CREATE | src/main/java/com/campusnexus/service/impl/CloudinaryServiceImpl.java | Cloudinary service implementation using Java SDK |
| Step-161 | CREATE | src/main/java/com/campusnexus/service/CloudinaryService.java | Cloudinary service interface for upload/delete operations |
| Step-160 | CREATE | src/main/java/com/campusnexus/config/CloudinaryConfig.java | Configuration bean to instantiate Cloudinary client |
| Step-159 | MODIFY | src/main/resources/application.properties | Added Cloudinary properties & adjusted multipart request limit to 25MB |
| Step-158 | MODIFY | pom.xml | Added Cloudinary dependency (1.39.0) |
| Step-157 | MODIFY | ../campus_connect_fronted/src/pages/student/ProfilePage.jsx | Fixed profile prefill, form reset, and file upload submit |
| Step-156 | MODIFY | ../campus_connect_fronted/src/api/student.api.js | Added profile picture and resume upload APIs |
| Step-155 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Added print statements for Fetching student profile and Authenticated user to getProfile method |
| Step-154 | MODIFY | src/main/java/com/campusnexus/service/impl/StudentProfileServiceImpl.java | Mapped the updatedAt field from the StudentProfile entity to the StudentProfileResponse DTO in both retrieval and update operations |
| Step-153 | MODIFY | src/main/java/com/campusnexus/dto/StudentProfileResponse.java | Added updatedAt field to StudentProfileResponse DTO to send profile update timestamp to the frontend |
| Step-152 | MODIFY | ../campus_connect_fronted/src/pages/student/TimetablePage.jsx | Added console logs for fetching, response, and mapped timetable |
| Step-151 | MODIFY | ../campus_connect_fronted/src/pages/hod/TimetablePage.jsx | Filter slots in CurrentTimetableTab and mapped fromTime/toTime in ManualAddModal |
| Step-150 | MODIFY | ../campus_connect_fronted/src/components/shared/TimetableGrid.jsx | Improved cell matching logic to support time/day normalizations |
| Step-149 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Added endpoint debug logging to timetable endpoint |
| Step-148 | MODIFY | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | Applied year/day normalizations and student timetable debug logs |
| Step-147 | MODIFY | src/main/java/com/campusnexus/dto/TimetableResponse.java | Added getFromTime/getToTime getters for backwards compatibility |
| Step-146 | MODIFY | src/main/java/com/campusnexus/dto/TimetableRequest.java | Added JsonAlias for startTime/fromTime and endTime/toTime compatibility |
| Step-145 | MODIFY | PROJECT_CONTEXT.md | Updated registries and change log for database constraint corrections |
| Step-144 | MODIFY | DataInitializer.java | Dropped NOT NULL constraints on from_time, to_time, start_time, and end_time columns at startup |
| Step-143 | MODIFY | PROJECT_CONTEXT.md | Updated registries and change log for automated constraint startup fix |
| Step-142 | MODIFY | DataInitializer.java | Added schema fix to drop NOT NULL constraint on timetables.batch_id during startup |
| Step-141 | MODIFY | PROJECT_CONTEXT.md | Updated registries and change log for timetable column mapping |
| Step-140 | MODIFY | Timetable.java | Mapped startTime/endTime to existing from_time/to_time columns to avoid SQL constraint violations |
| Step-139 | MODIFY | PROJECT_CONTEXT.md | Updated registries and change log for timetable publish fixes |
| Step-138 | MODIFY | TimetableServiceImpl.java / TimetableRepository.java | Fixed publish flow payload, validation, and clash check by status |
| Step-137 | MODIFY | PROJECT_CONTEXT.md | Updated registries and change log for HOD professors endpoint |
| Step-136 | MODIFY | HODController.java | Added GET /api/hod/professors endpoint |
| Step-135 | MODIFY | PROJECT_CONTEXT.md | Updated all registries for Timetable AI module |
| Step-134 | MODIFY | application.properties | Added groq.api.key, groq.api.url, groq.model properties |
| Step-133 | MODIFY | ProfessorController.java | Added TimetableService injection, GET /timetable and GET /timetable/merged endpoints |
| Step-132 | MODIFY | StudentController.java | Updated GET /timetable to require year, semester, division params |
| Step-131 | MODIFY | HODController.java | Added ai-suggest, publish, archive, archived endpoints; modified GET/DELETE timetable |
| Step-130 | MODIFY | TimetableServiceImpl.java | Full rewrite with Groq AI integration, conflict detection, all new timetable methods |
| Step-129 | MODIFY | TimetableService.java | Added 9 new method signatures to interface |
| Step-128 | MODIFY | TeacherAvailabilityRepository.java | Added findByTeacher_IdAndStatusIn query method |
| Step-127 | MODIFY | TimetableRepository.java | Added 5 new status-filtered query methods |
| Step-126 | CREATE | RestTemplateConfig.java | RestTemplate bean for Groq HTTP calls |
| Step-125 | CREATE | AISuggestRequest.java | AI suggestion DTO with TeacherSubjectMapping inner class |
| Step-124 | MODIFY | TimetableResponse.java | Added departmentId, teacherId, status, hasConflict, conflictReason, type fields |
| Step-123 | MODIFY | TimetableRequest.java | Replaced with departmentId, teacherId, year, semester, division, startTime, endTime |
| Step-122 | MODIFY | Timetable.java | Added year, semester, division, status, teacherName; changed to startTime/endTime strings |
| Step-121 | CREATE | TimetableStatus.java | New enum: DRAFT, PUBLISHED, ARCHIVED, DELETED |
| Step-120 | MODIFY | StudentController.java | Added profile picture and resume upload endpoints |
| Step-119 | MODIFY | StudentController.java | Added profile picture and resume upload endpoints to Firebase |
| Step-118 | MODIFY | StudentProfileServiceImpl.java | Fixed profile synchronization issues (merged User/Profile fields properly) |
| Step-117 | MODIFY | StudentProfileServiceImpl.java | Fixed response mapping to ensure User fields (name, phone) are always returned |
| Step-116 | MODIFY | StudentController.java | Added GET profile and updated PUT profile to use StudentProfileService |
| Step-115 | CREATE | StudentProfileServiceImpl.java | Implemented profile CRUD logic |
| Step-114 | CREATE | StudentProfileService.java | Added service interface for profile management |
| Step-113 | CREATE | StudentProfileResponse.java | Added DTO for consolidated profile response |
| Step-112 | CREATE | UpdateStudentProfileRequest.java | Added DTO for extended profile updates |
| Step-111 | CREATE | StudentProfileRepository.java | Added repository with findByUserId |
| Step-110 | CREATE | StudentProfile.java | Added student details entity with OneToOne relationship to User |
| Step-109 | CREATE | StringListConverter.java | Added JSON converter for List<String> fields |
| Step-108 | MODIFY | EventServiceImpl.java | Applied global sorting logic and updated repository calls |
| Step-107 | MODIFY | EventRepository.java | Added OrderByStartDateTimeDesc to all event finder methods |
| Step-106 | MODIFY | ProfessorController.java | Added deleteAvailability endpoint |
| Step-105 | MODIFY | TeacherAvailabilityServiceImpl.java | Implemented deleteAvailability and validation logic |
| Step-104 | MODIFY | TeacherAvailabilityService.java | Added deleteAvailability method to interface |
| Step-103 | MODIFY | SubmissionResponse.java | Added teamMemberNames and teamSize fields to DTO |
| Step-102 | MODIFY | SubmissionServiceImpl.java | Updated mapToResponse to populate teamMemberNames and teamSize |
| Step-101 | MODIFY | BatchServiceImpl.java | Added filtering in getSectionsForStudent to exclude submitted tasks |
| Step-100 | MODIFY | SubmissionRepository.java | Added existsBySectionAndUserOrTeamMember for team-aware task check |
| Step-99 | MODIFY | SubmissionServiceImpl.java | Added team-based duplicate validation and updated student submission retrieval |
| Step-98 | MODIFY | SubmissionRepository.java | Added existsBySectionAndStudent and findByStudentIdOrTeamMemberId |
| Step-97 | MODIFY | ProfessorController.java | Added update/delete API endpoints for Batches and Sections |
| Step-96 | MODIFY | BatchServiceImpl.java | Implemented update/delete logic for Batches and Sections |
| Step-95 | MODIFY | BatchService.java | Added update/delete methods for Batches and Sections |
| Step-94 | MODIFY | BatchSectionCreateRequest.java | Added isActive field for section updates |
| Step-9 | MODIFY | BatchServiceImpl.java | Added deadline mapping logic |
| Step-8 | MODIFY | SectionResponse.java | Added deadline field |
| Step-7 | MODIFY | BatchSectionCreateRequest.java | Added deadline field |
| Step-6 | MODIFY | BatchSection.java | Added LocalDateTime deadline field |
| Step-5 | MODIFY | StudentController.java | Added GET /api/student/sections endpoint |
| Step-4 | MODIFY | BatchServiceImpl.java | Implemented getSectionsForStudent & updated mapping |
| Step-3 | MODIFY | BatchService.java | Added getSectionsForStudent to interface |
| Step-2 | MODIFY | BatchSectionRepository.java | Added findByBatchDepartmentId query |
| Step-1 | MODIFY | SectionResponse.java | Added batchName and teacherName to SectionResponse |
| Step-1 | CREATE | pom.xml | Maven config with Spring Boot 4.0.3 and all deps |
| Step-2 | CREATE | application.properties | All config properties |
| Step-3 | CREATE | CampusNexusApplication.java | Main class |
| Step-4 | CREATE | 15 enum files | All enums |
| Step-5 | CREATE | 17 entity files | All JPA entities |
| Step-6 | CREATE | 17 repository files | All JPA repositories |
| Step-7 | CREATE | 22 request DTO files | All request DTOs |
| Step-8 | CREATE | 17 response DTO + ApiResponse | All response DTOs |
| Step-9 | CREATE | 5 exception + GlobalExceptionHandler | Exception handling |
| Step-10 | CREATE | 4 security files | JWT, filter, UserDetails, entry point |
| Step-11 | CREATE | 5 config files | Security, Swagger, Firebase, Stripe, DataInitializer |
| Step-12 | CREATE | 16 service interfaces | All service contracts |
| Step-13 | CREATE | 19 service implementations | All business logic + Firebase + Stripe + FCM |
| Step-14 | CREATE | 7 controller files | All REST controllers |
| Step-15 | CREATE | PROJECT_CONTEXT.md | This file |
| Step-16 | MODIFY | pom.xml | User updated stripe-java from 26.13.0 to 31.1.0 |
| Step-17 | MODIFY | StripeConfig.java | Replaced Stripe.apiKey with StripeClient bean |
| Step-18 | MODIFY | StripeService.java | Updated to use stripeClient.paymentIntents().create() for v31 API |
| Step-19 | CREATE | CorsConfig.java | New highest-priority CorsFilter bean for preflight request handling |
| Step-20 | MODIFY | SecurityConfig.java | Fixed CORS with setAllowedOriginPatterns and CorsConfigurationSource bean |
| Step-21 | MODIFY | SwaggerConfig.java | Added localhost and ngrok servers, added @Value ngrok URL property |
| Step-22 | MODIFY | application.properties | Added ngrok URL property, forward-headers-strategy, Swagger CORS props |
| Step-23 | MODIFY | All 7 Controllers | Added @CrossOrigin(origins="*", allowedHeaders="*") at class level |
| Step-24 | MODIFY | PROJECT_CONTEXT.md | Updated File Registry/Change Log for CORS fixes |
| Step-25 | MODIFY | admin.api.js | Fixed updateCollegeStatus parameter format |
| Step-26 | MODIFY | professor.api.js | Added path variables to getSubmissions & getStudentProgress |
| Step-27 | MODIFY | student.api.js | Added sectionId to submitWork, removed getMyClubs |
| Step-28 | MODIFY | admin/EventsPage.jsx | Mapped form date/time fields to backend ISO startDateTime/endDateTime |
| Step-29 | MODIFY | principal/EventsPage.jsx | Mapped form date/time fields to ISO startDateTime/endDateTime |
| Step-30 | MODIFY | hod/EventsPage.jsx | Mapped form date/time fields to ISO startDateTime/endDateTime |
| Step-31 | MODIFY | professor/EventsPage.jsx | Mapped form date/time fields to ISO startDateTime/endDateTime and added eventType |
| Step-32 | MODIFY | admin/SeminarHallsPage.jsx | Renamed form input from facilities to amenities |
| Step-33 | MODIFY | principal/SeminarHallsPage.jsx | Renamed form input from facilities to amenities |
| Step-34 | MODIFY | hod/SeminarHallsPage.jsx | Renamed form input from facilities to amenities |
| Step-35 | MODIFY | admin/BroadcastPage.jsx | Renamed payload field broadcastLevel to level |
| Step-36 | MODIFY | principal/BroadcastPage.jsx | Renamed payload field broadcastLevel to level |
| Step-37 | MODIFY | hod/BroadcastPage.jsx | Renamed payload field broadcastLevel to level |
| Step-38 | MODIFY | professor/BatchesPage.jsx | Mapped batchName, removed subject, added departmentId input. Updated section schema to include title, type, and date |
| Step-39 | MODIFY | student/ClubsPage.jsx | Replaced broken getMyClubs with local filtering of allClubs |
| Step-40 | MODIFY | student/SubmissionsPage.jsx | Added sectionId input to submission form to match API signature |
| Step-41 | MODIFY | DepartmentCreateRequest.java | Removed `@NotNull` from `collegeId` validation |
| Step-42 | MODIFY | PrincipalController.java | Automatically set `collegeId` in `createDepartment` endpoint from authenticated Profile |
| Step-43 | MODIFY | principal/DepartmentsPage.jsx | Replaced text-based Assign HOD form with a searchable "Select User" dropdown exactly matching Admin Assign Principal UX |
| Step-44 | CREATE | API_ENDPOINTS.md | Generated detailed documentation of all 76 endpoints and DTOs |
| Step-45 | CREATE | generate_api.py | Generated script to extract Java code endpoints |
| Step-46 | CREATE | api_dump.txt | Raw extracted endpoints data |
| Step-47 | CREATE | generate_md.py | Generated formatter script to build the endpoint doc |
| Step-48 | MODIFY | src/main/java/com/campusnexus/dto/EventRegistrationResponse.java | Added eventId parameter |
| Step-49 | MODIFY | src/main/java/com/campusnexus/service/impl/EventRegistrationServiceImpl.java | Mapped eventId in registration response |
| Step-50 | CREATE | src/main/java/com/campusnexus/dto/EventParticipantResponse.java | Event participant DTO |
| Step-51 | MODIFY | src/main/java/com/campusnexus/service/EventRegistrationService.java | Added getParticipants |
| Step-52 | MODIFY | src/main/java/com/campusnexus/service/impl/EventRegistrationServiceImpl.java | Implemented getParticipants |
| Step-53 | MODIFY | src/main/java/com/campusnexus/repository/EventRegistrationRepository.java | Added findByEventId |
| Step-54 | CREATE | src/main/java/com/campusnexus/dto/UpdateEventStatusRequest.java | Update event status request DTO |
| Step-55 | MODIFY | src/main/java/com/campusnexus/service/EventService.java | Added updateEventStatus |
| Step-56 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Implemented updateEventStatus & auto-update logic |
| Step-57 | MODIFY | src/main/java/com/campusnexus/controller/ProfessorController.java | Added EventParticipant and Status update API endpoints |
| Step-58 | MODIFY | src/main/java/com/campusnexus/controller/HODController.java | Added EventParticipant and Status update API endpoints |
| Step-59 | MODIFY | src/main/java/com/campusnexus/controller/PrincipalController.java | Added EventParticipant and Status update API endpoints |
| Step-60 | MODIFY | src/main/java/com/campusnexus/controller/CampusAdminController.java | Added EventParticipant and Status update API endpoints |
| Step-61 | CREATE | src/main/java/com/campusnexus/controller/EventController.java | Added global Event participant and status endpoints |
| Step-62 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Implemented event level auto-assignment & validation |
| Step-63 | MODIFY | src/main/java/com/campusnexus/controller/PrincipalController.java | Enforced COLLEGE level for principal event creation |
| Step-64 | MODIFY | src/main/java/com/campusnexus/controller/HODController.java | Enforced DEPARTMENT level for HOD event creation |
| Step-65 | MODIFY | src/main/java/com/campusnexus/entity/Event.java | Added registrations association with @JsonIgnore |
| Step-66 | MODIFY | src/main/java/com/campusnexus/entity/EventRegistration.java | Added @JsonIgnore to event to break circularity |
| Step-67 | MODIFY | src/main/java/com/campusnexus/entity/Event.java | Added @JsonIgnore to all relational fields (college, dept, club, user) to prevent StackOverflowError |
| Step-68 | MODIFY | src/main/java/com/campusnexus/entity/EventRegistration.java | Added @JsonIgnore to student field to break circularity |
| Step-69 | MODIFY | src/main/java/com/campusnexus/dto/EventResponse.java | Added participantCount field to DTO |
| Step-70 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Mapped registeredCount to participantCount in mapToResponse |
| Step-71 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Added logging to getEvents to monitor response volume |
| Step-72 | MODIFY | Multiple Entities | Implemented explicit @EqualsAndHashCode(id only) to prevent recursion in collections |
| Step-73 | MODIFY | Multiple Entities | Applied class-level @JsonIgnoreProperties to break recursive serialization |
| Step-74 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Implemented Step B debug return (returning First Event ID) |
| Step-75 | MODIFY | src/main/java/com/campusnexus/dto/EventResponse.java | Added isRegistered flag to DTO |
| Step-76 | MODIFY | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | Implemented isRegistered mapping logic for students |
| Step-77 | MODIFY | src/main/java/com/campusnexus/controller/StudentController.java | Restored events endpoint with studentId passing |
| Step-78 | MODIFY | src/pages/student/EventsPage.jsx | Refactored UI to use isRegistered and optimistic updates |
| Step-79 | MODIFY | StudentController.java | Added GET /api/student/professors endpoint and updated UserSearchResponse mapping |
| Step-80 | MODIFY | src/main/java/com/campusnexus/dto/ClubResponse.java | Added isMember and isOwner flags |
| Step-81 | MODIFY | src/main/java/com/campusnexus/service/impl/ClubServiceImpl.java | Added getCurrentUserId helper and mapped isMember/isOwner in mapToResponse |
| Step-82 | MODIFY | src/main/java/com/campusnexus/dto/BroadcastResponse.java | Added senderRole field to DTO |
| Step-83 | MODIFY | src/main/java/com/campusnexus/service/impl/BroadcastServiceImpl.java | Implemented senderRole mapping logic and handled CAMPUS_ADMIN display |
| Step-84 | MODIFY | src/main/java/com/campusnexus/service/ClubService.java | Updated getPendingClubs to getClubRequests with status filter |
| Step-85 | MODIFY | src/main/java/com/campusnexus/service/impl/ClubServiceImpl.java | Implemented status filtering logic for club requests |
| Step-86 | MODIFY | src/main/java/com/campusnexus/controller/HODController.java | Supported status filter in GET /api/hod/club-requests |
| Step-87 | MODIFY | src/main/java/com/campusnexus/controller/PrincipalController.java | Supported status filter in GET /api/principal/club-requests |
| Step-88 | MODIFY | API_ENDPOINTS.md | Updated club request filtering documentation |
| Step-89 | MODIFY | PROJECT_CONTEXT.md | Updated Service Registry for ClubService method rename |
| Step-90 | MODIFY | BatchCreateRequest.java | Removed departmentId field for auto-assignment |
| Step-91 | MODIFY | BatchService.java | Updated createBatch signature to accept User professor |
| Step-92 | MODIFY | BatchServiceImpl.java | Auto-assign dept from professor, added validation, cleaned unused repos |
| Step-93 | MODIFY | ProfessorController.java | Extracted professor entity and passed to batch service |

---

## Known Issues / TODOs

| # | Issue | File | Priority |
|---|-------|------|----------|
| 1 | Spring Boot 4.0.3 not yet published to Maven Central | pom.xml | High — change to 3.4.x if needed for immediate compilation |
| 2 | Firebase service account JSON must be placed manually | src/main/resources/ | Medium |
| 3 | Stripe API keys must be replaced with real keys | application.properties | Medium |
| 4 | SMTP credentials must be configured | application.properties | Medium |
| 5 | PostgreSQL database must be created manually | DB | Medium |
| 6 | app.ngrok-url in application.properties must be updated every time ngrok restarts (free tier) | application.properties | Low |

---

## Integration Notes

### Firebase Setup Required
- Place `firebase-service-account.json` in `src/main/resources/`
- Set `firebase.storage-bucket` in application.properties

### Stripe Setup Required
- Replace `stripe.api-key` with your test/live key
- Register webhook endpoint: POST /api/webhook/stripe in Stripe dashboard
- Set `stripe.webhook-secret` from Stripe webhook signing secret

### PostgreSQL Setup Required
- Create database: `CREATE DATABASE campus_nexus;`
- Update datasource username/password in application.properties

### Groq AI Setup Required
- Replace `groq.api.key` with your Groq API key (get from https://console.groq.com)
- Default model: `llama-3.3-70b-versatile` (can be changed in application.properties)
- Used for AI-powered timetable generation via POST /api/hod/timetable/ai-suggest

---

## Final Summary
- Total Files Created: 180 (+3 from this session: EventStatusUtil.java, normalize_ticket_prices.sql, and updated EventServiceImpl/EventService/EventController/EventCreateRequest/EventRegistrationServiceImpl/ExternalEventServiceImpl/FirebaseStorageService)
- Total Entities: 19
- Total Endpoints: 113 (+1: DELETE /api/events/{eventId})
- Total Enums: 16
- Total Services: 22 (19 interfaces + 3 standalone)
- Build Command: mvn clean install
- Run Command: mvn spring-boot:run
- Swagger URL: http://localhost:8080/swagger-ui.html
- Default Admin Login: admin@campusnexus.com / Admin@123

---

## Event Module Fix Notes (Step-215 to Step-222)

### Issue #1 — Free Event Price
- `ticketPrice` now validated with `@DecimalMin("0.00")` in `EventCreateRequest`
- All `mapToResponse` calls normalize any negative stored price to `0` at read time
- One-time SQL migration `normalize_ticket_prices.sql` cleans legacy `ticket_price = -1` records
- Standard: `0 = FREE`, `> 0 = PAID`, negatives forbidden

### Issue #2 — Event Status
- Removed `checkAndUpdateStatus()` which wrote to DB on every GET
- All status now computed via `EventStatusUtil.compute(event)` — a pure static method using UTC datetimes
- CANCELLED is the only DB-persisted status override (user action)
- UPCOMING, ONGOING, COMPLETED transition automatically based on server UTC time
- `updateEventStatus` endpoint now rejects anything other than CANCELLED with HTTP 400

### Issue #3 — Registration Validation
- `EventRegistrationServiceImpl.registerForEvent()` now rejects COMPLETED and CANCELLED events
- `ExternalEventServiceImpl.registerGuest()` has the same guard
- Both throw `BadRequestException` with descriptive messages
- Guard runs BEFORE duplicate and capacity checks

### Issue #4 — Owner Delete
- New `DELETE /api/events/{eventId}` endpoint in `EventController`
- Service verifies caller is the event creator (throws `UnauthorizedException` otherwise)
- Cascade order: sub-event external regs → sub-event internal regs → sub-events → parent external regs → parent internal regs → Firebase image → event
- Firebase cleanup is best-effort: failure logs a warning but does not abort deletion
