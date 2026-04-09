package com.campusnexus.repository;

import com.campusnexus.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotesRepository extends JpaRepository<Notes, UUID> {
    List<Notes> findByUploadedById(UUID uploadedById);
    List<Notes> findByDepartmentId(UUID departmentId);
    List<Notes> findByDepartmentIdAndYearAndSemester(UUID departmentId, Integer year, Integer semester);
    List<Notes> findByDepartmentIdAndSubject(UUID departmentId, String subject);
}
