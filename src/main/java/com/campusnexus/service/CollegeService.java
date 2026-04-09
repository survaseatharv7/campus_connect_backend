package com.campusnexus.service;

import com.campusnexus.dto.*;
import com.campusnexus.enums.CollegeStatus;

import java.util.List;
import java.util.UUID;

public interface CollegeService {
    CollegeResponse createCollege(CollegeCreateRequest request, UUID createdById);
    List<CollegeResponse> getAllColleges();
    CollegeResponse approveCollege(UUID collegeId);
    CollegeResponse updateCollegeStatus(UUID collegeId, CollegeStatus status);
    CollegeResponse assignPrincipal(UUID collegeId, AssignPrincipalRequest request);
}
