package com.campusnexus.repository;

import com.campusnexus.entity.Timetable;
import com.campusnexus.enums.TimetableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID> {
    List<Timetable> findByDepartmentId(UUID departmentId);
    List<Timetable> findByBatchId(UUID batchId);

    List<Timetable> findByDepartment_IdAndStatus(UUID deptId, TimetableStatus status);

    List<Timetable> findByTeacher_IdAndDayOfWeekAndStatus(UUID teacherId, String dayOfWeek, TimetableStatus status);

    List<Timetable> findByDepartment_IdAndYearAndSemesterAndDivisionAndStatus(
            UUID deptId, String year, int semester, String division, TimetableStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Timetable t WHERE t.department.id = :departmentId AND t.year = :year AND t.semester = :semester AND UPPER(t.division) = UPPER(:division) AND t.status = com.campusnexus.enums.TimetableStatus.PUBLISHED")
    List<Timetable> findPublishedForStudent(
            @org.springframework.data.repository.query.Param("departmentId") UUID departmentId,
            @org.springframework.data.repository.query.Param("year") String year,
            @org.springframework.data.repository.query.Param("semester") int semester,
            @org.springframework.data.repository.query.Param("division") String division
    );

    List<Timetable> findByTeacher_IdAndStatus(UUID teacherId, TimetableStatus status);

    boolean existsByTeacher_IdAndDayOfWeekAndStartTimeAndStatusNot(
            UUID teacherId, String day, String startTime, TimetableStatus status);

    boolean existsByTeacher_IdAndDayOfWeekAndStartTimeAndStatus(
            UUID teacherId, String dayOfWeek, String startTime, TimetableStatus status);
}
