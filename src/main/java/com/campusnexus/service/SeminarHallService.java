package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface SeminarHallService {
    SeminarHallResponse createSeminarHall(SeminarHallRequest request);
    List<SeminarHallResponse> getPublicSeminarHalls();
    List<SeminarHallResponse> getSeminarHallsByCollege(UUID collegeId);
    List<SeminarHallResponse> getSeminarHallsByDepartment(UUID departmentId);
    SeminarHallResponse updateSeminarHall(UUID hallId, SeminarHallRequest request);
}
