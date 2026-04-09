package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.TimetableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final DepartmentRepository departmentRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    public TimetableServiceImpl(TimetableRepository timetableRepository,
                                 DepartmentRepository departmentRepository,
                                 BatchRepository batchRepository,
                                 UserRepository userRepository) {
        this.timetableRepository = timetableRepository;
        this.departmentRepository = departmentRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TimetableResponse createTimetable(TimetableRequest request, UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Timetable timetable = Timetable.builder()
                .department(department)
                .batch(batch)
                .dayOfWeek(request.getDayOfWeek())
                .fromTime(request.getFromTime())
                .toTime(request.getToTime())
                .subject(request.getSubject())
                .teacher(teacher)
                .room(request.getRoom())
                .build();

        timetable = timetableRepository.save(timetable);
        return mapToResponse(timetable);
    }

    @Override
    public List<TimetableResponse> getTimetableByDepartment(UUID departmentId) {
        return timetableRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TimetableResponse updateTimetable(UUID id, TimetableRequest request) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found"));

        timetable.setDayOfWeek(request.getDayOfWeek());
        timetable.setFromTime(request.getFromTime());
        timetable.setToTime(request.getToTime());
        timetable.setSubject(request.getSubject());
        timetable.setRoom(request.getRoom());

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            timetable.setTeacher(teacher);
        }

        timetable = timetableRepository.save(timetable);
        return mapToResponse(timetable);
    }

    @Override
    @Transactional
    public void deleteTimetable(UUID id) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found"));
        timetableRepository.delete(timetable);
    }

    private TimetableResponse mapToResponse(Timetable timetable) {
        return TimetableResponse.builder()
                .id(timetable.getId())
                .dayOfWeek(timetable.getDayOfWeek())
                .fromTime(timetable.getFromTime())
                .toTime(timetable.getToTime())
                .subject(timetable.getSubject())
                .teacherName(timetable.getTeacher().getName())
                .batchName(timetable.getBatch().getBatchName())
                .room(timetable.getRoom())
                .build();
    }
}
