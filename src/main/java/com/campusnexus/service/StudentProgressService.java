package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface StudentProgressService {
    ProgressResponse updateProgress(UUID studentId, StudentProgressRequest request, UUID updatedById);
    List<ProgressResponse> getProgressByStudent(UUID studentId);
}
