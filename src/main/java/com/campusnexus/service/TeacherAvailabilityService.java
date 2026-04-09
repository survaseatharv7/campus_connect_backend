package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface TeacherAvailabilityService {
    AvailabilityResponse createAvailability(TeacherAvailabilityRequest request, UUID teacherId);
    List<AvailabilityResponse> getAvailabilityByTeacher(UUID teacherId);
    AvailabilityResponse updateAvailability(UUID id, TeacherAvailabilityRequest request, UUID teacherId);
    void deleteAvailability(UUID id, UUID teacherId);
    List<AvailabilityResponse> getAvailabilityByDepartment(UUID departmentId);
}
