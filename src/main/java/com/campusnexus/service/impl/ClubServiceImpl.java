package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.ClubStatus;
import com.campusnexus.enums.Role;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.ClubService;
import com.campusnexus.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ClubServiceImpl(ClubRepository clubRepository,
                           UserRepository userRepository,
                           NotificationService notificationService) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ClubResponse createClub(ClubCreateRequest request, UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        User guideTeacher = userRepository.findById(request.getGuideTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Guide teacher not found"));

        if (guideTeacher.getRole() != Role.PROFESSOR) {
            throw new BadRequestException("Guide must be a professor");
        }

        Club club = Club.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .college(student.getCollege())
                .department(student.getDepartment())
                .createdBy(student)
                .guideTeacher(guideTeacher)
                .status(ClubStatus.PENDING_HOD)
                .build();

        club.getMembers().add(student);
        club = clubRepository.save(club);
        return mapToResponse(club, studentId);
    }

    @Override
    public List<ClubResponse> getClubsByCollege(UUID collegeId) {
        UUID currentUserId = getCurrentUserId();
        return clubRepository.findByCollegeId(collegeId).stream()
                .map(club -> mapToResponse(club, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClubResponse approveByHOD(UUID clubId, UUID hodId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        if (club.getStatus() != ClubStatus.PENDING_HOD) {
            throw new BadRequestException("Club is not pending HOD approval");
        }

        User hod = userRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        club.setStatus(ClubStatus.PENDING_PRINCIPAL);
        club.setHodApprovedBy(hod);
        club = clubRepository.save(club);
        return mapToResponse(club, hodId);
    }

    @Override
    @Transactional
    public ClubResponse rejectByHOD(UUID clubId, UUID hodId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        User hod = userRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        club.setStatus(ClubStatus.REJECTED);
        club.setHodApprovedBy(hod);
        club = clubRepository.save(club);

        notificationService.sendEmail(
                club.getCreatedBy().getEmail(),
                "Club Request Rejected",
                "Your club '" + club.getName() + "' has been rejected by the HOD."
        );

        return mapToResponse(club, hodId);
    }

    @Override
    @Transactional
    public ClubResponse approveByPrincipal(UUID clubId, UUID principalId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        if (club.getStatus() != ClubStatus.PENDING_PRINCIPAL) {
            throw new BadRequestException("Club is not pending principal approval");
        }

        User principal = userRepository.findById(principalId)
                .orElseThrow(() -> new ResourceNotFoundException("Principal not found"));

        club.setStatus(ClubStatus.APPROVED);
        club.setPrincipalApprovedBy(principal);
        club = clubRepository.save(club);

        notificationService.sendEmail(
                club.getCreatedBy().getEmail(),
                "Club Approved!",
                "Your club '" + club.getName() + "' has been approved. Congratulations!"
        );

        return mapToResponse(club, principalId);
    }

    @Override
    @Transactional
    public ClubResponse rejectByPrincipal(UUID clubId, UUID principalId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        User principal = userRepository.findById(principalId)
                .orElseThrow(() -> new ResourceNotFoundException("Principal not found"));

        club.setStatus(ClubStatus.REJECTED);
        club.setPrincipalApprovedBy(principal);
        club = clubRepository.save(club);

        notificationService.sendEmail(
                club.getCreatedBy().getEmail(),
                "Club Request Rejected",
                "Your club '" + club.getName() + "' has been rejected by the Principal."
        );

        return mapToResponse(club, principalId);
    }

    @Override
    public List<ClubResponse> getClubRequestsForHOD(UUID departmentId, String status) {
        UUID currentUserId = getCurrentUserId();
        ClubStatus clubStatus = ClubStatus.PENDING_HOD;
        if (status != null) {
            try {
                clubStatus = ClubStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }
        return clubRepository.findByDepartmentIdAndStatus(departmentId, clubStatus).stream()
                .map(club -> mapToResponse(club, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClubResponse> getClubRequestsForPrincipal(UUID collegeId, String status) {
        UUID currentUserId = getCurrentUserId();
        ClubStatus clubStatus = ClubStatus.PENDING_PRINCIPAL;
        if (status != null) {
            try {
                clubStatus = ClubStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }
        return clubRepository.findByCollegeIdAndStatus(collegeId, clubStatus).stream()
                .map(club -> mapToResponse(club, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClubResponse joinClub(UUID clubId, UUID studentId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        if (club.getStatus() != ClubStatus.APPROVED) {
            throw new BadRequestException("Club is not approved yet");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (club.getMembers().contains(student)) {
            throw new BadRequestException("Already a member of this club");
        }

        club.getMembers().add(student);
        club = clubRepository.save(club);
        return mapToResponse(club, studentId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    private ClubResponse mapToResponse(Club club, UUID currentUserId) {
        boolean isOwner = false;
        boolean isMember = false;
        
        if (currentUserId != null) {
            isOwner = club.getCreatedBy() != null && currentUserId.equals(club.getCreatedBy().getId());
            isMember = isOwner || (club.getMembers() != null && club.getMembers().stream()
                    .anyMatch(member -> currentUserId.equals(member.getId())));
        }

        return ClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .logoUrl(club.getLogoUrl())
                .status(club.getStatus())
                .statusLabel(club.getStatus().name().replace("_", " "))
                .guideName(club.getGuideTeacher() != null ? club.getGuideTeacher().getName() : null)
                .memberCount(club.getMembers() != null ? club.getMembers().size() : 0)
                .createdByName(club.getCreatedBy().getName())
                .createdAt(club.getCreatedAt())
                .isOwner(isOwner)
                .isMember(isMember)
                .build();
    }
}
