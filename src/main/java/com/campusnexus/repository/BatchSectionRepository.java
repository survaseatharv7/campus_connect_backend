package com.campusnexus.repository;

import com.campusnexus.entity.BatchSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BatchSectionRepository extends JpaRepository<BatchSection, UUID> {
    List<BatchSection> findByBatchId(UUID batchId);
    List<BatchSection> findByBatchDepartmentIdAndIsActiveTrueOrderByDeadlineDateAsc(UUID departmentId);
}
