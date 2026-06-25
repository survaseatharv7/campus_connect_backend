package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface NotesService {
    NotesResponse uploadNotes(NotesUploadRequest request, UUID professorId);
    List<NotesResponse> getNotesByProfessor(UUID professorId);
    List<NotesResponse> getNotesByDepartment(UUID departmentId, Integer year, String subject);
    NotesResponse updateNotes(UUID noteId, NotesUploadRequest request, UUID professorId);
    void deleteNotes(UUID noteId, UUID professorId);
    List<NotesResponse> getNotesByYearSemesterDivision(Integer year, Integer semester, String division, UUID departmentId);
}
