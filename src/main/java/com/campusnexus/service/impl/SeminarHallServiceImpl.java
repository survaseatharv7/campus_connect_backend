package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.enums.SeminarHallType;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.SeminarHallService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeminarHallServiceImpl implements SeminarHallService {

    private final SeminarHallRepository seminarHallRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;

    public SeminarHallServiceImpl(SeminarHallRepository seminarHallRepository,
                                  CollegeRepository collegeRepository,
                                  DepartmentRepository departmentRepository) {
        this.seminarHallRepository = seminarHallRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public SeminarHallResponse createSeminarHall(SeminarHallRequest request) {
        SeminarHall hall = SeminarHall.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .amenities(request.getAmenities())
                .hallType(request.getHallType())
                .isActive(true)
                .build();

        // collegeId is optional for PUBLIC halls created by admin
        if (request.getCollegeId() != null) {
            College college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found"));
            hall.setCollege(college);
        }

        if (request.getDepartmentId() != null) {
            hall.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }

        hall = seminarHallRepository.save(hall);
        return mapToResponse(hall);
    }

    @Override
    public List<SeminarHallResponse> getPublicSeminarHalls() {
        return seminarHallRepository.findByHallType(SeminarHallType.PUBLIC).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarHallResponse> getSeminarHallsByCollege(UUID collegeId) {
        return seminarHallRepository.findByCollegeId(collegeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarHallResponse> getSeminarHallsByDepartment(UUID departmentId) {
        return seminarHallRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeminarHallResponse updateSeminarHall(UUID hallId, SeminarHallRequest request) {
        SeminarHall hall = seminarHallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar hall not found"));

        hall.setName(request.getName());
        hall.setCapacity(request.getCapacity());
        hall.setLocation(request.getLocation());
        hall.setAmenities(request.getAmenities());
        hall.setHallType(request.getHallType());

        hall = seminarHallRepository.save(hall);
        return mapToResponse(hall);
    }

    private SeminarHallResponse mapToResponse(SeminarHall hall) {
        return SeminarHallResponse.builder()
                .id(hall.getId())
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .location(hall.getLocation())
                .amenities(hall.getAmenities())
                .hallType(hall.getHallType())
                .collegeName(hall.getCollege() != null ? hall.getCollege().getName() : null)
                .departmentName(hall.getDepartment() != null ? hall.getDepartment().getName() : null)
                .isActive(hall.getIsActive())
                .build();
    }
}