package com.campusnexus.repository;

import com.campusnexus.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollegeRepository extends JpaRepository<College, UUID> {
    boolean existsByUniqueCollegeCode(String uniqueCollegeCode);
    Optional<College> findByUniqueCollegeCode(String uniqueCollegeCode);
}
