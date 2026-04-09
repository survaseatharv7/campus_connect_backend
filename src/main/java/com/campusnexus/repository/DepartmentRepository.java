package com.campusnexus.repository;

import com.campusnexus.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByCollegeId(UUID collegeId);
    boolean existsByNameAndCollegeId(String name, UUID collegeId);
    Optional<Department> findByHodId(UUID hodId);
}