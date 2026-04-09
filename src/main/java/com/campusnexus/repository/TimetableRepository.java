package com.campusnexus.repository;

import com.campusnexus.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID> {
    List<Timetable> findByDepartmentId(UUID departmentId);
    List<Timetable> findByBatchId(UUID batchId);
}
