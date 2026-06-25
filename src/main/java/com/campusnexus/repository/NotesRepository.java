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

    @org.springframework.data.jpa.repository.Query("SELECT n FROM Notes n WHERE n.department.id = :departmentId AND n.year = :year AND n.semester = :semester AND (n.division IS NULL OR n.division = :division)")
    List<Notes> findRelevantNotes(
            @org.springframework.data.repository.query.Param("departmentId") UUID departmentId,
            @org.springframework.data.repository.query.Param("year") Integer year,
            @org.springframework.data.repository.query.Param("semester") Integer semester,
            @org.springframework.data.repository.query.Param("division") String division
    );
}
