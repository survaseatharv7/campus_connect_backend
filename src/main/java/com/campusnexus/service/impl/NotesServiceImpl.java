package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.NotesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final UserRepository userRepository;

    public NotesServiceImpl(NotesRepository notesRepository, UserRepository userRepository) {
        this.notesRepository = notesRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public NotesResponse uploadNotes(NotesUploadRequest request, UUID professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));

        Notes notes = Notes.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .fileUrl(request.getFileUrl())
                .subject(request.getSubject())
                .year(request.getYear())
                .semester(request.getSemester())
                .division(request.getDivision())
                .department(professor.getDepartment())
                .uploadedBy(professor)
                .build();

        notes = notesRepository.save(notes);
        return mapToResponse(notes);
    }

    @Override
    public List<NotesResponse> getNotesByProfessor(UUID professorId) {
        return notesRepository.findByUploadedById(professorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotesResponse> getNotesByDepartment(UUID departmentId, Integer year, String subject) {
        if (subject != null && !subject.isEmpty()) {
            return notesRepository.findByDepartmentIdAndSubject(departmentId, subject).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        if (year != null) {
            return notesRepository.findByDepartmentIdAndYearAndSemester(departmentId, year, null).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return notesRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotesResponse updateNotes(UUID noteId, NotesUploadRequest request, UUID professorId) {
        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notes not found"));

        if (!notes.getUploadedBy().getId().equals(professorId)) {
            throw new BadRequestException("You can only update your own notes");
        }

        notes.setTitle(request.getTitle());
        notes.setDescription(request.getDescription());
        notes.setFileUrl(request.getFileUrl());
        notes.setSubject(request.getSubject());
        notes.setYear(request.getYear());
        notes.setSemester(request.getSemester());
        notes.setDivision(request.getDivision());

        notes = notesRepository.save(notes);
        return mapToResponse(notes);
    }

    @Override
    @Transactional
    public void deleteNotes(UUID noteId, UUID professorId) {
        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notes not found"));

        if (!notes.getUploadedBy().getId().equals(professorId)) {
            throw new BadRequestException("You can only delete your own notes");
        }

        notesRepository.delete(notes);
    }

    @Override
    public List<NotesResponse> getNotesByYearSemesterDivision(Integer year, Integer semester, String division, UUID departmentId) {
        return notesRepository.findRelevantNotes(departmentId, year, semester, division).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotesResponse mapToResponse(Notes notes) {
        String yearLabel = null;
        if (notes.getYear() != null) {
            yearLabel = String.valueOf(notes.getYear());
        }
        return NotesResponse.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .description(notes.getDescription())
                .fileUrl(notes.getFileUrl())
                .subject(notes.getSubject())
                .year(notes.getYear())
                .semester(notes.getSemester())
                .division(notes.getDivision())
                .yearLabel(yearLabel)
                .uploaderName(notes.getUploadedBy().getName())
                .departmentName(notes.getDepartment().getName())
                .uploadedAt(notes.getUploadedAt())
                .build();
    }
}
