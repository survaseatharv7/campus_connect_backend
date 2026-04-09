package com.campusnexus.repository;

import com.campusnexus.entity.SeminarHall;
import com.campusnexus.enums.SeminarHallType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeminarHallRepository extends JpaRepository<SeminarHall, UUID> {
    List<SeminarHall> findByHallType(SeminarHallType hallType);
    List<SeminarHall> findByCollegeId(UUID collegeId);
    List<SeminarHall> findByDepartmentId(UUID departmentId);
}
