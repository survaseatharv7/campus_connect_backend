package com.campusnexus.repository;

import com.campusnexus.entity.Broadcast;
import com.campusnexus.enums.BroadcastLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, UUID> {
    List<Broadcast> findBySentById(UUID sentById);
    List<Broadcast> findByLevel(BroadcastLevel level);

    @Query("SELECT b FROM Broadcast b WHERE b.level = 'CAMPUS' " +
           "OR (b.level = 'COLLEGE' AND b.college.id = :collegeId) " +
           "OR (b.level = 'DEPARTMENT' AND b.department.id = :departmentId) " +
           "ORDER BY b.sentAt DESC")
    List<Broadcast> findRelevantBroadcasts(@Param("collegeId") UUID collegeId,
                                           @Param("departmentId") UUID departmentId);
}
