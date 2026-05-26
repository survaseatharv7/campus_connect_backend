package com.campusnexus.service.impl;

import com.campusnexus.dto.StudentProfileResponse;
import com.campusnexus.dto.UpdateStudentProfileRequest;
import com.campusnexus.entity.StudentProfile;
import com.campusnexus.entity.User;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.StudentProfileRepository;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ALWAYS set from User core fields
        StudentProfileResponse response = StudentProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePicUrl(user.getProfilePicUrl())
                .build();

        // IF profile exists: map all profile fields
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            response.setYear(profile.getYear());
            response.setSemester(profile.getSemester());
            response.setRollNumber(profile.getRollNumber());
            response.setDivision(profile.getDivision());
            response.setSkills(profile.getSkills());
            response.setInterests(profile.getInterests());
            response.setBio(profile.getBio());
            response.setGithubUrl(profile.getGithubUrl());
            response.setLinkedinUrl(profile.getLinkedinUrl());
            response.setPortfolioUrl(profile.getPortfolioUrl());
            response.setResumeUrl(profile.getResumeUrl());
            response.setUpdatedAt(profile.getUpdatedAt());
        });

        return response;
    }

    @Override
    @Transactional
    public StudentProfileResponse updateStudentProfile(UUID userId, UpdateStudentProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Update Core User Entity
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getProfilePicUrl() != null) user.setProfilePicUrl(request.getProfilePicUrl());
        userRepository.save(user);

        // 2. Upsert StudentProfile Entity
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> StudentProfile.builder().user(user).build());

        if (request.getYear() != null) profile.setYear(request.getYear());
        if (request.getSemester() != null) profile.setSemester(request.getSemester());
        if (request.getRollNumber() != null) profile.setRollNumber(request.getRollNumber());
        if (request.getDivision() != null) profile.setDivision(request.getDivision());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getInterests() != null) profile.setInterests(request.getInterests());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getResumeUrl() != null) profile.setResumeUrl(request.getResumeUrl());

        profileRepository.save(profile);

        // Map final response using consolidated User + Profile data
        return StudentProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePicUrl(user.getProfilePicUrl())
                .year(profile.getYear())
                .semester(profile.getSemester())
                .rollNumber(profile.getRollNumber())
                .division(profile.getDivision())
                .skills(profile.getSkills())
                .interests(profile.getInterests())
                .bio(profile.getBio())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .resumeUrl(profile.getResumeUrl())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
