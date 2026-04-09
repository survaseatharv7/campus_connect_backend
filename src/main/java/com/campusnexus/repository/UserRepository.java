package com.campusnexus.repository;

import com.campusnexus.entity.User;
import com.campusnexus.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndCollegeId(Role role, UUID collegeId);

    List<User> findByRoleAndDepartmentId(Role role, UUID departmentId);

    List<User> findByCollegeId(UUID collegeId);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    long countByRoleAndIsActiveTrue(Role role);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "u.phone LIKE CONCAT('%', :query, '%')")
    List<User> searchByNameEmailOrPhone(@Param("query") String query);
}
