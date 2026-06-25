package com.campusnexus.repository;

import com.campusnexus.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByBatchSectionId(UUID batchSectionId);

    @Query("SELECT s FROM Submission s LEFT JOIN s.teamMembers m WHERE s.student.id = :studentId OR m.id = :studentId")
    List<Submission> findByStudentIdOrTeamMemberId(UUID studentId);

    @Query("SELECT COUNT(s) > 0 FROM Submission s LEFT JOIN s.teamMembers m WHERE s.batchSection.id = :sectionId AND (s.student.id = :studentId OR m.id = :studentId)")
    boolean existsBySectionAndStudent(UUID sectionId, UUID studentId);

    @Query("""
    SELECT COUNT(s) > 0 FROM Submission s
    WHERE s.batchSection.id = :sectionId
    AND (
      s.student.id = :userId
      OR :userId IN (SELECT tm.id FROM s.teamMembers tm)
    )
    """)
    boolean existsBySectionAndUserOrTeamMember(UUID sectionId, UUID userId);
}


