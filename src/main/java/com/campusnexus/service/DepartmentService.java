package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentCreateRequest request);
    List<DepartmentResponse> getDepartmentsByCollege(UUID collegeId);
    DepartmentResponse assignHOD(UUID departmentId, AssignHODRequest request);
}
