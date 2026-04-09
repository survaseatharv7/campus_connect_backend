package com.campusnexus.repository;

import com.campusnexus.entity.Club;
import com.campusnexus.enums.ClubStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findByCollegeId(UUID collegeId);
    List<Club> findByDepartmentIdAndStatus(UUID departmentId, ClubStatus status);
    List<Club> findByCollegeIdAndStatus(UUID collegeId, ClubStatus status);
    long count();
}
