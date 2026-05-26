package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.TeacherAvailabilityStatus;
import com.campusnexus.enums.TimetableStatus;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.TimetableService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableServiceImpl implements TimetableService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TimetableServiceImpl.class);

    private final TimetableRepository timetableRepository;
    private final DepartmentRepository departmentRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    public TimetableServiceImpl(TimetableRepository timetableRepository,
                                 DepartmentRepository departmentRepository,
                                 BatchRepository batchRepository,
                                 UserRepository userRepository,
                                 TeacherAvailabilityRepository teacherAvailabilityRepository,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper) {
        this.timetableRepository = timetableRepository;
        this.departmentRepository = departmentRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
        this.teacherAvailabilityRepository = teacherAvailabilityRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public TimetableResponse createTimetable(TimetableRequest request, UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Timetable timetable = Timetable.builder()
                .department(department)
                .dayOfWeek(normalizeDay(request.getDayOfWeek()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .subject(request.getSubject())
                .teacher(teacher)
                .teacherName(teacher.getName())
                .year(normalizeYear(request.getYear()))
                .semester(request.getSemester())
                .division(request.getDivision())
                .status(TimetableStatus.PUBLISHED)
                .build();

        timetable = timetableRepository.save(timetable);
        return mapToResponse(timetable);
    }

    @Override
    public List<TimetableResponse> getTimetableByDepartment(UUID departmentId) {
        return timetableRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TimetableResponse updateTimetable(UUID id, TimetableRequest request) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found"));

        timetable.setDayOfWeek(normalizeDay(request.getDayOfWeek()));
        timetable.setStartTime(request.getStartTime());
        timetable.setEndTime(request.getEndTime());
        timetable.setSubject(request.getSubject());
        timetable.setYear(normalizeYear(request.getYear()));
        timetable.setSemester(request.getSemester());
        timetable.setDivision(request.getDivision());

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            timetable.setTeacher(teacher);
            timetable.setTeacherName(teacher.getName());
        }

        timetable = timetableRepository.save(timetable);
        return mapToResponse(timetable);
    }

    @Override
    @Transactional
    public void deleteTimetable(UUID id) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found"));
        timetableRepository.delete(timetable);
    }

    // ==================== NEW AI-POWERED METHODS ====================

    @Override
    public List<TimetableResponse> generateAISuggestion(AISuggestRequest request, UUID departmentId) {
        String normalizedYear = normalizeYear(request.getYear());
        // 1. Build existing committed slots for each teacher
        StringBuilder existingSlots = new StringBuilder();
        StringBuilder unavailablePeriods = new StringBuilder();

        for (AISuggestRequest.TeacherSubjectMapping mapping : request.getTeacherSubjectMappings()) {
            // Fetch PUBLISHED timetable slots for this teacher
            List<Timetable> publishedSlots = timetableRepository
                    .findByTeacher_IdAndStatus(mapping.getTeacherId(), TimetableStatus.PUBLISHED);
            for (Timetable slot : publishedSlots) {
                existingSlots.append("- ").append(mapping.getTeacherName())
                        .append(": ").append(slot.getDayOfWeek())
                        .append(" ").append(slot.getStartTime())
                        .append("-").append(slot.getEndTime())
                        .append(" (already teaching ").append(slot.getSubject()).append(")\n");
            }

            // Fetch BUSY / ON_LEAVE availability records
            List<TeacherAvailability> busyRecords = teacherAvailabilityRepository
                    .findByTeacher_IdAndStatusIn(mapping.getTeacherId(),
                            List.of(TeacherAvailabilityStatus.BUSY, TeacherAvailabilityStatus.ON_LEAVE));
            for (TeacherAvailability avail : busyRecords) {
                unavailablePeriods.append("- ").append(mapping.getTeacherName())
                        .append(": ").append(avail.getDate().getDayOfWeek().name())
                        .append(" ").append(avail.getFromTime())
                        .append("-").append(avail.getToTime())
                        .append(" (").append(avail.getStatus().name()).append(")\n");
            }
        }

        // 2. Build teacher-subject assignment lines
        StringBuilder teacherAssignments = new StringBuilder();
        for (AISuggestRequest.TeacherSubjectMapping mapping : request.getTeacherSubjectMappings()) {
            teacherAssignments.append("- ").append(mapping.getTeacherName())
                    .append(" (ID: ").append(mapping.getTeacherId()).append("): ")
                    .append(mapping.getSubject()).append(", ")
                    .append(mapping.getLecturesPerWeek()).append(" lectures/week\n");
        }

        // 3. Build the prompt
        String prompt = String.format("""
                Generate a weekly university timetable as a JSON array.
                
                Target: Year=%s, Division=%s, Semester=%d
                
                Teacher-Subject assignments:
                %s
                Working days: %s
                Time slots available: %s
                
                Existing committed slots for these teachers (HARD CONSTRAINT - never schedule at these times):
                %s
                Unavailable periods (HARD CONSTRAINT - teacher marked busy or on leave):
                %s
                Rules:
                1. Never schedule a teacher at a time listed under existing committed slots or unavailable periods
                2. No division should have two subjects at the same time slot
                3. Distribute each subject's lectures across different days of the week
                4. Each time slot for this division should have at most one subject
                
                Return ONLY a valid JSON array with no explanation, no markdown, no code fences. Each element must have exactly these fields:
                "year", "division", "semester", "dayOfWeek", "startTime", "endTime", "subject", "teacherId", "teacherName"
                """,
                normalizedYear,
                request.getDivision(),
                request.getSemester(),
                teacherAssignments.toString(),
                String.join(", ", request.getWorkingDays()),
                String.join(", ", request.getTimeSlots()),
                existingSlots.length() > 0 ? existingSlots.toString() : "None\n",
                unavailablePeriods.length() > 0 ? unavailablePeriods.toString() : "None\n"
        );

        // 4. Call Groq API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqModel);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 4000);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content",
                        "You are a university timetable scheduler. Always respond with valid JSON array only, no explanation, no markdown."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> groqResponse;
        try {
            groqResponse = restTemplate.exchange(groqApiUrl, HttpMethod.POST, httpEntity, Map.class);
        } catch (Exception e) {
            throw new BadRequestException("Failed to connect to AI service: " + e.getMessage());
        }

        // 5. Parse response
        String content;
        try {
            Map responseBody = groqResponse.getBody();
            List<Map> choices = (List<Map>) responseBody.get("choices");
            Map message = (Map) choices.get(0).get("message");
            content = (String) message.get("content");
        } catch (Exception e) {
            throw new BadRequestException("AI returned an unexpected response format");
        }

        // 6. Parse content as List<TimetableResponse>
        List<TimetableResponse> suggestions;
        try {
            suggestions = objectMapper.readValue(content, new TypeReference<List<TimetableResponse>>() {});
        } catch (Exception e) {
            throw new BadRequestException("AI returned invalid response, please try again");
        }

        // 7. Map subjects to assignments to recover IDs/names, override target fields, set status = DRAFT on each, and run conflict validation
        Map<String, AISuggestRequest.TeacherSubjectMapping> subjectMap = request.getTeacherSubjectMappings().stream()
                .collect(Collectors.toMap(
                        m -> m.getSubject().trim().toLowerCase(),
                        m -> m,
                        (existing, replacement) -> existing
                ));

        for (TimetableResponse slot : suggestions) {
            slot.setStatus(TimetableStatus.DRAFT);
            slot.setType("TEACHING");
            slot.setDepartmentId(departmentId);
            slot.setYear(normalizedYear);
            slot.setSemester(request.getSemester());
            slot.setDivision(request.getDivision());
            slot.setDayOfWeek(normalizeDay(slot.getDayOfWeek()));

            // Recover teacher details from original assignments mapping if matching by subject
            if (slot.getSubject() != null) {
                String subKey = slot.getSubject().trim().toLowerCase();
                AISuggestRequest.TeacherSubjectMapping mapping = subjectMap.get(subKey);
                if (mapping != null) {
                    slot.setTeacherId(mapping.getTeacherId());
                    slot.setTeacherName(mapping.getTeacherName());
                }
            }

            // Check if teacher already has a PUBLISHED slot at same day+time
            if (slot.getTeacherId() != null) {
                List<Timetable> existingAtSlot = timetableRepository
                        .findByTeacher_IdAndDayOfWeekAndStatus(
                                slot.getTeacherId(), normalizeDay(slot.getDayOfWeek()), TimetableStatus.PUBLISHED);
                for (Timetable existing : existingAtSlot) {
                    if (existing.getStartTime().equals(slot.getStartTime())) {
                        slot.setHasConflict(true);
                        slot.setConflictReason("Teacher " + slot.getTeacherName() +
                                " already has a class at " + slot.getDayOfWeek() +
                                " " + slot.getStartTime() + " (teaching " + existing.getSubject() + ")");
                        break;
                    }
                }
            }
        }

        return suggestions;
    }

    @Override
    @Transactional
    public List<TimetableResponse> publishTimetable(List<TimetableRequest> slots, UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        List<TimetableResponse> results = new ArrayList<>();

        for (TimetableRequest slot : slots) {
            // Defensive validation to prevent unhandled 500 DB constraint errors
            if (slot.getTeacherId() == null) {
                throw new BadRequestException("Teacher ID is required for subject: " + slot.getSubject());
            }
            if (slot.getYear() == null || slot.getYear().isBlank()) {
                throw new BadRequestException("Year is required for subject: " + slot.getSubject());
            }
            if (slot.getDayOfWeek() == null || slot.getDayOfWeek().isBlank()) {
                throw new BadRequestException("Day of week is required for subject: " + slot.getSubject());
            }
            if (slot.getStartTime() == null || slot.getStartTime().isBlank()) {
                throw new BadRequestException("Start time is required for subject: " + slot.getSubject());
            }
            if (slot.getEndTime() == null || slot.getEndTime().isBlank()) {
                throw new BadRequestException("End time is required for subject: " + slot.getSubject());
            }
            if (slot.getSubject() == null || slot.getSubject().isBlank()) {
                throw new BadRequestException("Subject is required");
            }

            String normalizedDay = normalizeDay(slot.getDayOfWeek());

            // Final clash check against active PUBLISHED slots only
            boolean hasClash = timetableRepository
                    .existsByTeacher_IdAndDayOfWeekAndStartTimeAndStatus(
                            slot.getTeacherId(), normalizedDay, slot.getStartTime(), TimetableStatus.PUBLISHED);
            if (hasClash) {
                throw new BadRequestException("Conflict detected: Teacher " + slot.getTeacherId() +
                        " already has a slot at " + slot.getDayOfWeek() + " " + slot.getStartTime());
            }

            User teacher = userRepository.findById(slot.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + slot.getTeacherId()));

            Timetable timetable = Timetable.builder()
                    .department(department)
                    .dayOfWeek(normalizedDay)
                    .startTime(slot.getStartTime())
                    .endTime(slot.getEndTime())
                    .subject(slot.getSubject())
                    .teacher(teacher)
                    .teacherName(teacher.getName())
                    .year(normalizeYear(slot.getYear()))
                    .semester(slot.getSemester())
                    .division(slot.getDivision())
                    .status(TimetableStatus.PUBLISHED)
                    .build();

            timetable = timetableRepository.save(timetable);
            results.add(mapToResponse(timetable));
        }

        return results;
    }

    @Override
    @Transactional
    public void archiveSemester(UUID departmentId, String year, int semester, String division) {
        String normalizedYear = normalizeYear(year);
        List<Timetable> slots = timetableRepository
                .findByDepartment_IdAndYearAndSemesterAndDivisionAndStatus(
                        departmentId, normalizedYear, semester, division, TimetableStatus.PUBLISHED);

        if (slots.isEmpty()) {
            throw new ResourceNotFoundException("No published timetable slots found for the given criteria");
        }

        for (Timetable slot : slots) {
            slot.setStatus(TimetableStatus.ARCHIVED);
        }
        timetableRepository.saveAll(slots);
    }

    @Override
    @Transactional
    public void deleteTimetableSlot(UUID slotId, UUID hodUserId) {
        Timetable timetable = timetableRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));

        // Verify slot belongs to HOD's department
        User hod = userRepository.findById(hodUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!timetable.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("This timetable slot does not belong to your department");
        }

        // Soft delete
        timetable.setStatus(TimetableStatus.DELETED);
        timetableRepository.save(timetable);
    }

    @Override
    public List<TimetableResponse> getTimetableForStudent(UUID departmentId, String year, int semester, String division) {
        String normalizedYear = normalizeYear(year);
        log.info("Fetching timetable for year={}, sem={}, div={}, deptId={}", normalizedYear, semester, division, departmentId);

        List<Timetable> result = timetableRepository.findPublishedForStudent(departmentId, normalizedYear, semester, division);

        log.info("Fetched student timetable count: {}", result.size());

        return result.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimetableResponse> getTimetableForProfessor(UUID teacherId) {
        return timetableRepository.findByTeacher_IdAndStatus(teacherId, TimetableStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimetableResponse> getTeacherScheduleMerged(UUID teacherId) {
        List<TimetableResponse> merged = new ArrayList<>();

        // 1. Add PUBLISHED timetable slots as "TEACHING"
        List<Timetable> teachingSlots = timetableRepository
                .findByTeacher_IdAndStatus(teacherId, TimetableStatus.PUBLISHED);
        for (Timetable slot : teachingSlots) {
            TimetableResponse resp = mapToResponse(slot);
            resp.setType("TEACHING");
            merged.add(resp);
        }

        // 2. Add TeacherAvailability records as "AVAILABILITY"
        List<TeacherAvailability> availabilities = teacherAvailabilityRepository.findByTeacherId(teacherId);
        for (TeacherAvailability avail : availabilities) {
            TimetableResponse resp = TimetableResponse.builder()
                    .id(avail.getId())
                    .teacherId(avail.getTeacher().getId())
                    .teacherName(avail.getTeacher().getName())
                    .dayOfWeek(avail.getDate().getDayOfWeek().name())
                    .startTime(avail.getFromTime().toString())
                    .endTime(avail.getToTime().toString())
                    .type("AVAILABILITY")
                    .build();
            merged.add(resp);
        }

        return merged;
    }

    @Override
    public List<TimetableResponse> getTimetableByDepartmentAndStatuses(UUID departmentId, List<TimetableStatus> statuses) {
        List<TimetableResponse> results = new ArrayList<>();
        for (TimetableStatus status : statuses) {
            results.addAll(
                    timetableRepository.findByDepartment_IdAndStatus(departmentId, status).stream()
                            .map(this::mapToResponse)
                            .collect(Collectors.toList())
            );
        }
        return results;
    }

    @Override
    public List<TimetableResponse> getArchivedByDepartment(UUID departmentId) {
        return timetableRepository.findByDepartment_IdAndStatus(departmentId, TimetableStatus.ARCHIVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== PRIVATE HELPERS ====================

    private String normalizeYear(String year) {
        if (year == null) return null;
        switch (year.trim().toUpperCase()) {
            case "1":
            case "FE":
                return "1";
            case "2":
            case "SE":
                return "2";
            case "3":
            case "TE":
                return "3";
            case "4":
            case "BE":
                return "4";
            default:
                return year.trim().toUpperCase();
        }
    }

    private String normalizeDay(String day) {
        if (day == null) return null;
        return day.trim().toUpperCase();
    }

    private TimetableResponse mapToResponse(Timetable timetable) {
        return TimetableResponse.builder()
                .id(timetable.getId())
                .departmentId(timetable.getDepartment().getId())
                .teacherId(timetable.getTeacher().getId())
                .teacherName(timetable.getTeacherName() != null ?
                        timetable.getTeacherName() : timetable.getTeacher().getName())
                .subject(timetable.getSubject())
                .year(timetable.getYear())
                .semester(timetable.getSemester())
                .division(timetable.getDivision())
                .dayOfWeek(timetable.getDayOfWeek())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .status(timetable.getStatus())
                .createdAt(timetable.getCreatedAt())
                .type("TEACHING")
                .build();
    }
}
