package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface TimetableService {
    TimetableResponse createTimetable(TimetableRequest request, UUID departmentId);
    List<TimetableResponse> getTimetableByDepartment(UUID departmentId);
    TimetableResponse updateTimetable(UUID id, TimetableRequest request);
    void deleteTimetable(UUID id);

    // New AI-powered timetable methods
    List<TimetableResponse> generateAISuggestion(AISuggestRequest request, UUID departmentId);
    List<TimetableResponse> publishTimetable(List<TimetableRequest> slots, UUID departmentId);
    void archiveSemester(UUID departmentId, String year, int semester, String division);
    void deleteTimetableSlot(UUID slotId, UUID hodUserId);
    List<TimetableResponse> getTimetableForStudent(UUID departmentId, String year, int semester, String division);
    List<TimetableResponse> getTimetableForProfessor(UUID teacherId);
    List<TimetableResponse> getTeacherScheduleMerged(UUID teacherId);
    List<TimetableResponse> getTimetableByDepartmentAndStatuses(UUID departmentId, List<com.campusnexus.enums.TimetableStatus> statuses);
    List<TimetableResponse> getArchivedByDepartment(UUID departmentId);
}
