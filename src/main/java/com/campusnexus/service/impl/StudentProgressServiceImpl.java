package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.StudentProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentProgressServiceImpl implements StudentProgressService {

    private final StudentProgressRepository progressRepository;
    private final UserRepository userRepository;

    public StudentProgressServiceImpl(StudentProgressRepository progressRepository,
                                       UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ProgressResponse updateProgress(UUID studentId, StudentProgressRequest request, UUID updatedById) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        User updatedBy = userRepository.findById(updatedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProgress progress = StudentProgress.builder()
                .student(student)
                .updatedBy(updatedBy)
                .progressNote(request.getProgressNote())
                .percentage(request.getPercentage())
                .subject(request.getSubject())
                .build();

        progress = progressRepository.save(progress);
        return mapToResponse(progress);
    }

    @Override
    public List<ProgressResponse> getProgressByStudent(UUID studentId) {
        return progressRepository.findByStudentIdOrderByUpdatedAtDesc(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProgressResponse mapToResponse(StudentProgress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .studentName(progress.getStudent().getName())
                .updatedByName(progress.getUpdatedBy().getName())
                .progressNote(progress.getProgressNote())
                .percentage(progress.getPercentage())
                .subject(progress.getSubject())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
