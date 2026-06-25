package com.campusnexus.service;

import com.campusnexus.dto.*;

import com.campusnexus.entity.User;
import java.util.List;
import java.util.UUID;

public interface BatchService {
    BatchResponse createBatch(BatchCreateRequest request, User professor);
    List<BatchResponse> getBatchesByProfessor(UUID professorId);
    SectionResponse createSection(UUID batchId, BatchSectionCreateRequest request);
    List<SectionResponse> getSectionsByBatch(UUID batchId);
    List<SectionResponse> getSectionsForStudent(UUID studentId);

    BatchResponse updateBatch(UUID batchId, BatchCreateRequest request, User professor);
    void deleteBatch(UUID batchId, User professor);
    SectionResponse updateSection(UUID sectionId, BatchSectionCreateRequest request, User professor);
    void deleteSection(UUID sectionId, User professor);
}
