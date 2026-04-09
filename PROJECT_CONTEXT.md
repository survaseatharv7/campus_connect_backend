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
| 1 | pom.xml | Config | ✅ Created | Maven build config with all dependencies |
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
| 26 | src/main/java/com/campusnexus/entity/Event.java | Entity | ✏️ Modified | Added @JsonIgnore to all relational fields to break cycles |
| 27 | src/main/java/com/campusnexus/entity/EventRegistration.java | Entity | ✏️ Modified | Added @JsonIgnore to student and event |
| 28 | src/main/java/com/campusnexus/entity/SeminarHall.java | Entity | ✅ Created | Seminar hall with type/capacity |
| 29 | src/main/java/com/campusnexus/entity/SeminarHallBooking.java | Entity | ✅ Created | Hall booking with time range |
| 30 | src/main/java/com/campusnexus/entity/Broadcast.java | Entity | ✅ Created | Multi-level broadcast messages |
| 31 | src/main/java/com/campusnexus/entity/Notes.java | Entity | ✅ Created | Professor notes uploads |
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
| 43 | src/main/java/com/campusnexus/repository/EventRepository.java | Repository | ✏️ Modified | Added sorting by startDateTime DESC to finder methods |
| 44 | src/main/java/com/campusnexus/repository/EventRegistrationRepository.java | Repository | ✅ Created | Registration queries with Stripe |
| 45 | src/main/java/com/campusnexus/repository/SeminarHallRepository.java | Repository | ✅ Created | SeminarHall queries |
| 46 | src/main/java/com/campusnexus/repository/SeminarHallBookingRepository.java | Repository | ✅ Created | Booking queries |
| 47 | src/main/java/com/campusnexus/repository/BroadcastRepository.java | Repository | ✅ Created | Broadcast queries with JPQL |
| 48 | src/main/java/com/campusnexus/repository/NotesRepository.java | Repository | ✅ Created | Notes queries |
| 49 | src/main/java/com/campusnexus/repository/TeacherAvailabilityRepository.java | Repository | ✅ Created | Availability queries |
| 50 | src/main/java/com/campusnexus/repository/TimetableRepository.java | Repository | ✅ Created | Timetable queries |
| 51 | src/main/java/com/campusnexus/repository/StudentProgressRepository.java | Repository | ✅ Created | Progress queries |
| 52 | src/main/java/com/campusnexus/repository/InvalidatedTokenRepository.java | Repository | ✅ Created | Token blocklist check |
| 53 | src/main/java/com/campusnexus/dto/LoginRequest.java | DTO | ✅ Created | Login DTO |
| 54 | src/main/java/com/campusnexus/dto/RegisterRequest.java | DTO | ✅ Created | Register DTO |
| 55 | src/main/java/com/campusnexus/dto/RefreshTokenRequest.java | DTO | ✅ Created | Refresh token DTO |
| 56 | src/main/java/com/campusnexus/dto/CollegeCreateRequest.java | DTO | ✅ Created | College creation |
| 57 | src/main/java/com/campusnexus/dto/DepartmentCreateRequest.java | DTO | ✅ Created | Department creation |
| 58 | src/main/java/com/campusnexus/dto/AssignPrincipalRequest.java | DTO | ✅ Created | Principal assignment |
| 59 | src/main/java/com/campusnexus/dto/AssignHODRequest.java | DTO | ✅ Created | HOD assignment |
| 60 | src/main/java/com/campusnexus/dto/EventCreateRequest.java | DTO | ✅ Created | Event creation |
| 60.1 | src/main/java/com/campusnexus/dto/UpdateEventStatusRequest.java | DTO | ✅ Created | Update event status |
| 61 | src/main/java/com/campusnexus/dto/EventRegistrationRequest.java | DTO | ✅ Created | Event registration |
| 62 | src/main/java/com/campusnexus/dto/ClubCreateRequest.java | DTO | ✅ Created | Club creation |
| 63 | src/main/java/com/campusnexus/dto/BatchCreateRequest.java | DTO | ✏️ Modified | Removed departmentId field |
| 64 | src/main/java/com/campusnexus/dto/BatchSectionCreateRequest.java | DTO | ✏️ Modified | Added deadline and isActive fields |
| 65 | src/main/java/com/campusnexus/dto/SubmissionRequest.java | DTO | ✅ Created | Submission creation |
| 66 | src/main/java/com/campusnexus/dto/SubmissionRemarkRequest.java | DTO | ✅ Created | Professor remark |
| 67 | src/main/java/com/campusnexus/dto/NotesUploadRequest.java | DTO | ✅ Created | Notes upload |
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
| 121 | src/main/java/com/campusnexus/dto/EventResponse.java | DTO | ✏️ Modified | Added isRegistered flag |
| 80 | src/main/java/com/campusnexus/dto/EventRegistrationResponse.java | DTO | ✏️ Modified | Registration/ticket response |
| 80.1 | src/main/java/com/campusnexus/dto/EventParticipantResponse.java | DTO | ✅ Created | Participants list response |
| 81 | src/main/java/com/campusnexus/dto/ClubResponse.java | DTO | ✏️ Modified | Added isMember and isOwner flags |
| 82 | src/main/java/com/campusnexus/dto/BatchResponse.java | DTO | ✅ Created | Batch response |
| 83 | src/main/java/com/campusnexus/dto/SectionResponse.java | DTO | ✏️ Modified | Added batchName and teacherName |
| 84 | src/main/java/com/campusnexus/dto/SubmissionResponse.java | DTO | ✏️ Modified | Added teamMemberNames and teamSize fields |
| 85 | src/main/java/com/campusnexus/dto/NotesResponse.java | DTO | ✅ Created | Notes response |
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
| 102 | src/main/java/com/campusnexus/config/SecurityConfig.java | Config | ✏️ Modified | Spring Security 7 config + CORS fix |
| 103 | src/main/java/com/campusnexus/config/SwaggerConfig.java | Config | ✏️ Modified | OpenAPI/Swagger config + ngrok servers |
| 104 | src/main/java/com/campusnexus/config/FirebaseConfig.java | Config | ✅ Created | Firebase init |
| 105 | src/main/java/com/campusnexus/config/StripeConfig.java | Config | ✏️ Modified | Stripe StripeClient bean |
| 149 | src/main/java/com/campusnexus/config/CorsConfig.java | Config | ✅ Created | Highest-priority CorsFilter bean |
| 106 | src/main/java/com/campusnexus/config/DataInitializer.java | Config | ✅ Created | Admin seeder |
| 107 | src/main/java/com/campusnexus/service/AuthService.java | Service | ✅ Created | Auth interface |
| 108 | src/main/java/com/campusnexus/service/CollegeService.java | Service | ✅ Created | College interface |
| 109 | src/main/java/com/campusnexus/service/DepartmentService.java | Service | ✅ Created | Department interface |
| 110 | src/main/java/com/campusnexus/service/EventService.java | Service | ✅ Created | Event interface |
| 111 | src/main/java/com/campusnexus/service/EventRegistrationService.java | Service | ✅ Created | Registration interface |
| 112 | src/main/java/com/campusnexus/service/ClubService.java | Service | ✏️ Modified | Added status filtering to getClubRequests for HOD/Principal |
| 113 | src/main/java/com/campusnexus/service/BatchService.java | Service | ✏️ Modified | Added getSectionsForStudent |
| 114 | src/main/java/com/campusnexus/service/SubmissionService.java | Service | ✅ Created | Submission interface |
| 115 | src/main/java/com/campusnexus/service/NotesService.java | Service | ✅ Created | Notes interface |
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
| 126 | src/main/java/com/campusnexus/service/impl/EventServiceImpl.java | ServiceImpl | ✏️ Modified | Applied global sorting by startDateTime DESC to all event methods |
| 127 | src/main/java/com/campusnexus/service/impl/EventRegistrationServiceImpl.java | ServiceImpl | ✏️ Modified | Registration + Stripe |
| 128 | src/main/java/com/campusnexus/service/impl/ClubServiceImpl.java | ServiceImpl | ✏️ Modified | Implemented status filtering logic for club requests |
| 129 | src/main/java/com/campusnexus/service/impl/BatchServiceImpl.java | ServiceImpl | ✏️ Modified | Implemented filtering in getSectionsForStudent for pending tasks |
| 130 | src/main/java/com/campusnexus/service/impl/SubmissionServiceImpl.java | ServiceImpl | ✏️ Modified | Updated mapping logic to include teamMemberNames and teamSize |
| 131 | src/main/java/com/campusnexus/service/impl/NotesServiceImpl.java | ServiceImpl | ✅ Created | Notes CRUD |
| 132 | src/main/java/com/campusnexus/service/impl/BroadcastServiceImpl.java | ServiceImpl | ✏️ Modified | Handled senderRole and Campus Admin display |
| 133 | src/main/java/com/campusnexus/service/impl/SeminarHallServiceImpl.java | ServiceImpl | ✅ Created | Seminar hall CRUD |
| 134 | src/main/java/com/campusnexus/service/impl/TimetableServiceImpl.java | ServiceImpl | ✅ Created | Timetable CRUD |
| 135 | src/main/java/com/campusnexus/service/impl/TeacherAvailabilityServiceImpl.java | ServiceImpl | ✅ Created | Availability CRUD |
| 136 | src/main/java/com/campusnexus/service/impl/StudentProgressServiceImpl.java | ServiceImpl | ✅ Created | Progress tracking |
| 137 | src/main/java/com/campusnexus/service/impl/DashboardServiceImpl.java | ServiceImpl | ✅ Created | Dashboard stats |
| 138 | src/main/java/com/campusnexus/service/impl/NotificationServiceImpl.java | ServiceImpl | ✅ Created | Email sending |
| 139 | src/main/java/com/campusnexus/service/impl/FirebaseStorageService.java | ServiceImpl | ✅ Created | Firebase file upload |
| 140 | src/main/java/com/campusnexus/service/impl/FCMService.java | ServiceImpl | ✅ Created | FCM push notifications |
| 141 | src/main/java/com/campusnexus/service/impl/StripeService.java | ServiceImpl | ✏️ Modified | Stripe v31 StripeClient + webhook |
| 142 | src/main/java/com/campusnexus/controller/AuthController.java | Controller | ✏️ Modified | Auth endpoints + @CrossOrigin |
| 143 | src/main/java/com/campusnexus/controller/CampusAdminController.java | Controller | ✏️ Modified | Admin endpoints + event participant/status |
| 144 | src/main/java/com/campusnexus/controller/PrincipalController.java | Controller | ✏️ Modified | Supported status filter in club requests endpoint |
| 145 | src/main/java/com/campusnexus/controller/HODController.java | Controller | ✏️ Modified | Supported status filter in club requests endpoint |
| 146 | src/main/java/com/campusnexus/controller/ProfessorController.java | Controller | ✏️ Modified | Professor endpoints + event participant/status |
| 147 | src/main/java/com/campusnexus/controller/StudentController.java | Controller | ✏️ Modified | Added GET /api/student/sections endpoint |
| 148 | src/main/java/com/campusnexus/controller/WebhookController.java | Controller | ✏️ Modified | Stripe webhook + @CrossOrigin |
| 148.1 | src/main/java/com/campusnexus/controller/EventController.java | Controller | ✅ Created | Global Event endpoints |
| 149 | ../campus_connect_frontend/src/api/admin.api.js | API | ✏️ Modified | Admin API client |
| 150 | ../campus_connect_frontend/src/api/professor.api.js | API | ✏️ Modified | Professor API client |
| 150 | ../campus_connect_fronted/src/pages/student/EventsPage.jsx | Page | ✏️ Modified | Refactored to use isRegistered and optimistic updates |
| 152 | API_ENDPOINTS.md | Documentation | ✏️ Modified | Updated club request filtering documentation |
| 153 | generate_api.py | Script | ✅ Created | Python script to extract endpoints |
| 154 | api_dump.txt | Data | ✅ Created | Extracted raw DTOs and endpoints |
| 155 | generate_md.py | Script | ✅ Created | Python script to format Markdown |

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
| Notes | notes | UUID | ManyToOne: uploadedBy(User), Department | ✅ |
| TeacherAvailability | teacher_availability | UUID | ManyToOne: teacher(User) | ✅ |
| Timetable | timetables | UUID | ManyToOne: Department, Batch, teacher(User) | ✅ |
| StudentProgress | student_progress | UUID | ManyToOne: student(User), updatedBy(User) | ✅ |
| InvalidatedToken | invalidated_tokens | UUID | None | ✅ |

---

## API Endpoint Registry

| Method | Endpoint | Controller | Role Required | Description | Status |
|--------|----------|-----------|---------------|-------------|--------|
| POST | /api/auth/register | AuthController | Public | Register user | ✅ |
| POST | /api/auth/login | AuthController | Public | Login | ✅ |
| POST | /api/auth/refresh | AuthController | Public | Refresh token | ✅ |
| POST | /api/auth/logout | AuthController | Public | Logout | ✅ |
| GET | /api/events/{eventId}/participants | EventController | All | List event participants | ✅ |
| PUT | /api/events/{eventId}/status | EventController | All | Update event status | ✅ |
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
| GET | /api/hod/timetable | HODController | HOD | List timetable | ✅ |
| PUT | /api/hod/timetable/{id} | HODController | HOD | Update timetable | ✅ |
| DELETE | /api/hod/timetable/{id} | HODController | HOD | Delete timetable | ✅ |
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
| GET | /api/student/timetable | StudentController | STUDENT | View timetable | ✅ |
| GET | /api/student/teacher-availability | StudentController | STUDENT | View availability | ✅ |
| GET | /api/student/progress | StudentController | STUDENT | View own progress | ✅ |
| GET | /api/student/sections | StudentController | STUDENT | List relevant sections | ✅ |
| PUT | /api/student/profile | StudentController | STUDENT | Update profile | ✅ |
| POST | /api/webhook/stripe | WebhookController | Public | Stripe webhook | ✅ |

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

---

## Service Registry

| Service Interface | Implementation Class | Methods | Status |
|------------------|---------------------|---------|--------|
| AuthService | AuthServiceImpl | register, login, refreshToken, logout | ✅ |
| CollegeService | CollegeServiceImpl | createCollege, getAllColleges, approveCollege, updateCollegeStatus, assignPrincipal | ✅ |
| DepartmentService | DepartmentServiceImpl | createDepartment, getDepartmentsByCollege, assignHOD | ✅ |
| EventService | EventServiceImpl | createEvent, getEventsByLevel/College/Department/Creator, getVisibleEventsForStudent, approveEvent, updateEventStatus | ✅ |
| EventRegistrationService | EventRegistrationServiceImpl | registerForEvent, getStudentRegistrations, getTicketDetails, getParticipants | ✅ |
| ClubService | ClubServiceImpl | createClub, getClubsByCollege, approveByHOD/Principal, rejectByHOD/Principal, getClubRequests, joinClub | ✅ |
| BatchService | BatchServiceImpl | createBatch, getBatchesByProfessor, createSection, getSectionsByBatch, getSectionsForStudent, updateBatch, deleteBatch, updateSection, deleteSection | ✅ |
| SubmissionService | SubmissionServiceImpl | createSubmission (validated), getSubmissionsBySection, getSubmissionsByStudent (team-aware), addRemark | ✅ |
| NotesService | NotesServiceImpl | uploadNotes, getNotesByProfessor/Department, updateNotes, deleteNotes | ✅ |
| BroadcastService | BroadcastServiceImpl | createBroadcast, getBroadcastsBySender, getRelevantBroadcasts | ✅ |
| SeminarHallService | SeminarHallServiceImpl | createSeminarHall, getPublic/ByCollege/ByDepartment, updateSeminarHall | ✅ |
| TimetableService | TimetableServiceImpl | createTimetable, getTimetableByDepartment, updateTimetable, deleteTimetable | ✅ |
| TeacherAvailabilityService | TeacherAvailabilityServiceImpl | createAvailability, getByTeacher/Department, updateAvailability, deleteAvailability | ✅ |
| StudentProgressService | StudentProgressServiceImpl | updateProgress, getProgressByStudent | ✅ |
| DashboardService | DashboardServiceImpl | getDashboardStats | ✅ |
| NotificationService | NotificationServiceImpl | sendEmail (async) | ✅ |
| — | FirebaseStorageService | uploadFile | ✅ |
| — | FCMService | sendToTopic, sendToToken | ✅ |
| — | StripeService | createPaymentIntent, handleWebhookEvent | ✅ |

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

---

## Change Log

| Timestamp | Action | File | Details |
|-----------|--------|------|---------|
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

---

## Final Summary
- Total Files Created: 155
- Total Entities: 17
- Total Endpoints: 93
- Total Enums: 15
- Total Services: 19 (16 interfaces + 3 standalone)
- Build Command: mvn clean install
- Run Command: mvn spring-boot:run
- Swagger URL: http://localhost:8080/swagger-ui.html
- Default Admin Login: admin@campusnexus.com / Admin@123
