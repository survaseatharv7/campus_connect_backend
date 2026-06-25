package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.TeacherAvailabilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeacherAvailabilityServiceImpl implements TeacherAvailabilityService {

    private final TeacherAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public TeacherAvailabilityServiceImpl(TeacherAvailabilityRepository availabilityRepository,
                                          UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AvailabilityResponse createAvailability(TeacherAvailabilityRequest request, UUID teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        TeacherAvailability availability = TeacherAvailability.builder()
                .teacher(teacher)
                .date(request.getDate())
                .fromTime(request.getFromTime())
                .toTime(request.getToTime())
                .status(request.getStatus())
                .note(request.getNote())
                .build();

        availability = availabilityRepository.save(availability);
        return mapToResponse(availability);
    }

    @Override
    public List<AvailabilityResponse> getAvailabilityByTeacher(UUID teacherId) {
        return availabilityRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvailabilityResponse updateAvailability(UUID id, TeacherAvailabilityRequest request, UUID teacherId) {
        TeacherAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        if (!availability.getTeacher().getId().equals(teacherId)) {
            throw new com.campusnexus.exception.UnauthorizedException("You are not authorized to update this availability");
        }

        if (request.getFromTime().isAfter(request.getToTime())) {
            throw new com.campusnexus.exception.BadRequestException("Start time must be before end time");
        }

        availability.setDate(request.getDate());
        availability.setFromTime(request.getFromTime());
        availability.setToTime(request.getToTime());
        availability.setStatus(request.getStatus());
        availability.setNote(request.getNote());

        availability = availabilityRepository.save(availability);
        return mapToResponse(availability);
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID id, UUID teacherId) {
        TeacherAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        if (!availability.getTeacher().getId().equals(teacherId)) {
            throw new com.campusnexus.exception.UnauthorizedException("You are not authorized to delete this availability");
        }

        availabilityRepository.delete(availability);
    }

    @Override
    public List<AvailabilityResponse> getAvailabilityByDepartment(UUID departmentId) {
        return availabilityRepository.findByTeacherDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AvailabilityResponse mapToResponse(TeacherAvailability availability) {
        return AvailabilityResponse.builder()
                .id(availability.getId())
                .teacherName(availability.getTeacher().getName())
                .date(availability.getDate())
                .fromTime(availability.getFromTime())
                .toTime(availability.getToTime())
                .status(availability.getStatus())
                .note(availability.getNote())
                .build();
    }
}
