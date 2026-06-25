package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.SubmissionStatus;
import com.campusnexus.enums.SubmissionType;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.NotificationService;
import com.campusnexus.service.SubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final BatchSectionRepository batchSectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                  BatchSectionRepository batchSectionRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.submissionRepository = submissionRepository;
        this.batchSectionRepository = batchSectionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public SubmissionResponse createSubmission(UUID sectionId, SubmissionRequest request, UUID studentId) {
        BatchSection section = batchSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Validation: Prevent duplicate submissions
        if (submissionRepository.existsBySectionAndStudent(sectionId, studentId)) {
            throw new BadRequestException("You have already submitted for this section.");
        }

        Submission submission = Submission.builder()
                .batchSection(section)
                .submissionType(request.getSubmissionType())
                .fileUrl(request.getFileUrl())
                .description(request.getDescription())
                .status(SubmissionStatus.SUBMITTED)
                .build();

        if (request.getSubmissionType() == SubmissionType.INDIVIDUAL) {
            submission.setStudent(student);
        } else {
            submission.setTeamName(request.getTeamName());
            submission.setStudent(student);
            
            if (request.getTeamMemberIds() != null && !request.getTeamMemberIds().isEmpty()) {
                List<User> teamMembers = new ArrayList<>();
                for (UUID memberId : request.getTeamMemberIds()) {
                    // Check if member already submitted
                    if (submissionRepository.existsBySectionAndStudent(sectionId, memberId)) {
                        User member = userRepository.findById(memberId)
                                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
                        throw new BadRequestException("Team member " + member.getName() + " has already submitted for this section.");
                    }

                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + memberId));
                    teamMembers.add(member);
                }
                submission.setTeamMembers(teamMembers);
            }
        }

        submission = submissionRepository.save(submission);
        return mapToResponse(submission);
    }

    @Override
    public List<SubmissionResponse> getSubmissionsBySection(UUID sectionId) {
        return submissionRepository.findByBatchSectionId(sectionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByStudent(UUID studentId) {
        return submissionRepository.findByStudentIdOrTeamMemberId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubmissionResponse addRemark(UUID submissionId, SubmissionRemarkRequest request, UUID professorId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));

        submission.setProfessorRemark(request.getRemark());
        submission.setStatus(request.getStatus());
        submission.setRemarkAt(LocalDateTime.now());
        submission.setReviewedBy(professor);

        submission = submissionRepository.save(submission);

        // Notify student about review
        if (submission.getStudent() != null) {
            notificationService.sendEmail(
                    submission.getStudent().getEmail(),
                    "Submission Reviewed",
                    "Your submission has been reviewed.\nStatus: " + request.getStatus() +
                    "\nRemark: " + request.getRemark()
            );
        }

        return mapToResponse(submission);
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        List<String> teamMemberNames = submission.getTeamMembers() != null
                ? submission.getTeamMembers().stream().map(User::getName).collect(Collectors.toList())
                : new ArrayList<>();

        int teamSize = 1; // Default to 1 (the student who submitted)
        if (submission.getTeamMembers() != null) {
            teamSize += submission.getTeamMembers().size();
        }

        return SubmissionResponse.builder()
                .id(submission.getId())
                .studentName(submission.getStudent() != null ? submission.getStudent().getName() : null)
                .teamName(submission.getTeamName())
                .teamMembers(teamMemberNames)
                .teamMemberNames(teamMemberNames)
                .teamSize(teamSize)
                .submissionType(submission.getSubmissionType())
                .fileUrl(submission.getFileUrl())
                .description(submission.getDescription())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus())
                .professorRemark(submission.getProfessorRemark())
                .remarkAt(submission.getRemarkAt())
                .reviewedByName(submission.getReviewedBy() != null ? submission.getReviewedBy().getName() : null)
                .build();
    }
}
