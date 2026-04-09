package com.campusnexus.repository;

import com.campusnexus.entity.TeacherAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, UUID> {
    List<TeacherAvailability> findByTeacherId(UUID teacherId);
    List<TeacherAvailability> findByTeacherDepartmentId(UUID departmentId);
}
