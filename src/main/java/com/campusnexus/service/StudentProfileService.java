package com.campusnexus.service;

import com.campusnexus.dto.StudentProfileResponse;
import com.campusnexus.dto.UpdateStudentProfileRequest;

import java.util.UUID;

public interface StudentProfileService {
    StudentProfileResponse getStudentProfile(UUID userId);
    StudentProfileResponse updateStudentProfile(UUID userId, UpdateStudentProfileRequest request);
}
