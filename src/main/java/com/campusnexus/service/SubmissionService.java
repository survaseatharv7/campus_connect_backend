package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface SubmissionService {
    SubmissionResponse createSubmission(UUID sectionId, SubmissionRequest request, UUID studentId);
    List<SubmissionResponse> getSubmissionsBySection(UUID sectionId);
    List<SubmissionResponse> getSubmissionsByStudent(UUID studentId);
    SubmissionResponse addRemark(UUID submissionId, SubmissionRemarkRequest request, UUID professorId);
}
