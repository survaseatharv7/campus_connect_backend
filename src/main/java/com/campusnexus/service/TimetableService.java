package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface TimetableService {
    TimetableResponse createTimetable(TimetableRequest request, UUID departmentId);
    List<TimetableResponse> getTimetableByDepartment(UUID departmentId);
    TimetableResponse updateTimetable(UUID id, TimetableRequest request);
    void deleteTimetable(UUID id);
}
