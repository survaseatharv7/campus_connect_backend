package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.College;
import com.campusnexus.entity.Department;
import com.campusnexus.entity.User;
import com.campusnexus.enums.Role;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.DuplicateResourceException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.CollegeRepository;
import com.campusnexus.repository.DepartmentRepository;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                  CollegeRepository collegeRepository,
                                  UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
        College college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        if (departmentRepository.existsByNameAndCollegeId(request.getName(), request.getCollegeId())) {
            throw new DuplicateResourceException("Department already exists in this college");
        }

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .college(college)
                .build();

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByCollege(UUID collegeId) {
        return departmentRepository.findByCollegeId(collegeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentResponse assignHOD(UUID departmentId, AssignHODRequest request) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        User newHod = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newHod.getRole() != Role.PROFESSOR && newHod.getRole() != Role.HOD) {
            throw new BadRequestException("User must be a PROFESSOR or HOD to be assigned as HOD");
        }

        // Downgrade previous HOD to PROFESSOR if exists and is different from new HOD
        User previousHod = department.getHod();
        if (previousHod != null && !previousHod.getId().equals(newHod.getId())) {
            previousHod.setRole(Role.PROFESSOR);
            userRepository.save(previousHod);
        }

        // Upgrade new HOD
        newHod.setRole(Role.HOD);
        newHod.setDepartment(department);
        userRepository.save(newHod);

        department.setHod(newHod);
        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .hodName(department.getHod() != null ? department.getHod().getName() : null)
                .collegeName(department.getCollege().getName())
                .collegeId(department.getCollege().getId())
                .createdAt(department.getCreatedAt())
                .build();
    }
}
