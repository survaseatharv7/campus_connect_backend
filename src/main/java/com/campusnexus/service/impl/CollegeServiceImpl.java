package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.College;
import com.campusnexus.entity.Department;
import com.campusnexus.entity.User;
import com.campusnexus.enums.CollegeStatus;
import com.campusnexus.enums.Role;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.CollegeRepository;
import com.campusnexus.repository.DepartmentRepository;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.CollegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public CollegeServiceImpl(CollegeRepository collegeRepository,
                              UserRepository userRepository,
                              DepartmentRepository departmentRepository) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public CollegeResponse createCollege(CollegeCreateRequest request, UUID createdById) {
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String uniqueCode = generateUniqueCollegeCode();

        College college = College.builder()
                .uniqueCollegeCode(uniqueCode)
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .logoUrl(request.getLogoUrl())
                .status(CollegeStatus.PENDING)
                .createdBy(creator)
                .build();

        college = collegeRepository.save(college);
        return mapToResponse(college);
    }

    @Override
    public List<CollegeResponse> getAllColleges() {
        return collegeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CollegeResponse approveCollege(UUID collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        college.setStatus(CollegeStatus.ACTIVE);
        college = collegeRepository.save(college);
        return mapToResponse(college);
    }

    @Override
    @Transactional
    public CollegeResponse updateCollegeStatus(UUID collegeId, CollegeStatus status) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        college.setStatus(status);
        college = collegeRepository.save(college);
        return mapToResponse(college);
    }

    @Override
    @Transactional
    public CollegeResponse assignPrincipal(UUID collegeId, AssignPrincipalRequest request) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        User newPrincipal = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getUserEmail()));

        // Allow PROFESSOR, HOD, or PRINCIPAL to be assigned as principal
        if (newPrincipal.getRole() != Role.PROFESSOR
                && newPrincipal.getRole() != Role.PRINCIPAL
                && newPrincipal.getRole() != Role.HOD) {
            throw new BadRequestException("User must be a PROFESSOR, HOD, or PRINCIPAL to be assigned as principal");
        }

        // If the new principal was previously a HOD, clear them from their department
        if (newPrincipal.getRole() == Role.HOD) {
            Optional<Department> hodDepartment = departmentRepository.findByHodId(newPrincipal.getId());
            if (hodDepartment.isPresent()) {
                Department dept = hodDepartment.get();
                dept.setHod(null);
                departmentRepository.save(dept);
            }
        }

        // Downgrade previous principal to PROFESSOR if exists and is different from new principal
        User previousPrincipal = college.getPrincipal();
        if (previousPrincipal != null && !previousPrincipal.getId().equals(newPrincipal.getId())) {
            previousPrincipal.setRole(Role.PROFESSOR);
            userRepository.save(previousPrincipal);
        }

        // Upgrade new principal
        newPrincipal.setRole(Role.PRINCIPAL);
        newPrincipal.setCollege(college);
        userRepository.save(newPrincipal);

        college.setPrincipal(newPrincipal);
        college = collegeRepository.save(college);
        return mapToResponse(college);
    }

    private String generateUniqueCollegeCode() {
        SecureRandom random = new SecureRandom();
        String code;
        do {
            StringBuilder sb = new StringBuilder("CLG-");
            for (int i = 0; i < 4; i++) {
                sb.append((char) ('A' + random.nextInt(26)));
            }
            sb.append("-").append(Year.now().getValue());
            code = sb.toString();
        } while (collegeRepository.existsByUniqueCollegeCode(code));
        return code;
    }

    private CollegeResponse mapToResponse(College college) {
        return CollegeResponse.builder()
                .id(college.getId())
                .uniqueCollegeCode(college.getUniqueCollegeCode())
                .name(college.getName())
                .address(college.getAddress())
                .city(college.getCity())
                .state(college.getState())
                .logoUrl(college.getLogoUrl())
                .status(college.getStatus())
                .principalName(college.getPrincipal() != null ? college.getPrincipal().getName() : null)
                .createdAt(college.getCreatedAt())
                .build();
    }
}