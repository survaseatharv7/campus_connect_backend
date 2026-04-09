package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.repository.*;
import com.campusnexus.service.BatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final BatchSectionRepository batchSectionRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public BatchServiceImpl(BatchRepository batchRepository,
                            BatchSectionRepository batchSectionRepository,
                            SubmissionRepository submissionRepository,
                            UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.batchSectionRepository = batchSectionRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BatchResponse createBatch(BatchCreateRequest request, User professor) {
        if (professor.getDepartment() == null) {
            throw new BadRequestException("Professor is not assigned to any department");
        }

        Batch batch = Batch.builder()
                .batchName(request.getBatchName())
                .year(request.getYear())
                .semester(request.getSemester())
                .department(professor.getDepartment())
                .professor(professor)
                .build();

        batch = batchRepository.save(batch);
        return mapToResponse(batch);
    }

    @Override
    public List<BatchResponse> getBatchesByProfessor(UUID professorId) {
        return batchRepository.findByProfessorId(professorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SectionResponse createSection(UUID batchId, BatchSectionCreateRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        BatchSection section = BatchSection.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .sectionType(request.getSectionType())
                .batch(batch)
                .deadlineDate(request.getDeadlineDate())
                .deadline(request.getDeadline())
                .isActive(true)
                .build();

        section = batchSectionRepository.save(section);
        return mapSectionToResponse(section);
    }

    @Override
    public List<SectionResponse> getSectionsByBatch(UUID batchId) {
        return batchSectionRepository.findByBatchId(batchId).stream()
                .map(this::mapSectionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SectionResponse> getSectionsForStudent(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (student.getDepartment() == null) {
            return List.of(); // Or throw exception, but empty list is safer for UI
        }
        
        return batchSectionRepository.findByBatchDepartmentIdAndIsActiveTrueOrderByDeadlineDateAsc(student.getDepartment().getId())
                .stream()
                .filter(section -> !submissionRepository.existsBySectionAndUserOrTeamMember(section.getId(), studentId))
                .map(this::mapSectionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BatchResponse updateBatch(UUID batchId, BatchCreateRequest request, User professor) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!batch.getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not authorized to update this batch");
        }

        batch.setBatchName(request.getBatchName());
        batch.setYear(request.getYear());
        batch.setSemester(request.getSemester());

        batch = batchRepository.save(batch);
        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public void deleteBatch(UUID batchId, User professor) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!batch.getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not authorized to delete this batch");
        }

        if (!batchSectionRepository.findByBatchId(batchId).isEmpty()) {
            throw new BadRequestException("Cannot delete batch with active sections. Delete sections first.");
        }

        batchRepository.delete(batch);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(UUID sectionId, BatchSectionCreateRequest request, User professor) {
        BatchSection section = batchSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getBatch().getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not authorized to update this section");
        }

        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setSectionType(request.getSectionType());
        section.setDeadlineDate(request.getDeadlineDate());
        section.setDeadline(request.getDeadline());
        if (request.getIsActive() != null) {
            section.setIsActive(request.getIsActive());
        }

        section = batchSectionRepository.save(section);
        return mapSectionToResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(UUID sectionId, User professor) {
        BatchSection section = batchSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getBatch().getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not authorized to delete this section");
        }

        if (!submissionRepository.findByBatchSectionId(sectionId).isEmpty()) {
            throw new BadRequestException("Cannot delete section with existing submissions.");
        }

        batchSectionRepository.delete(section);
    }

    private BatchResponse mapToResponse(Batch batch) {
        int sectionCount = batchSectionRepository.findByBatchId(batch.getId()).size();
        return BatchResponse.builder()
                .id(batch.getId())
                .batchName(batch.getBatchName())
                .year(batch.getYear())
                .semester(batch.getSemester())
                .departmentName(batch.getDepartment().getName())
                .professorName(batch.getProfessor().getName())
                .sectionCount(sectionCount)
                .createdAt(batch.getCreatedAt())
                .build();
    }

    private SectionResponse mapSectionToResponse(BatchSection section) {
        int submissionCount = submissionRepository.findByBatchSectionId(section.getId()).size();
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .description(section.getDescription())
                .sectionType(section.getSectionType())
                .deadlineDate(section.getDeadlineDate())
                .isActive(section.getIsActive())
                .submissionCount(submissionCount)
                .batchName(section.getBatch().getBatchName())
                .teacherName(section.getBatch().getProfessor().getName())
                .deadline(section.getDeadline())
                .build();
    }
}
