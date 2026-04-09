package com.campusnexus.repository;

import com.campusnexus.entity.Event;
import com.campusnexus.enums.EventLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByEventLevelOrderByStartDateTimeDesc(EventLevel eventLevel);
    List<Event> findByCollegeIdOrderByStartDateTimeDesc(UUID collegeId);
    List<Event> findByDepartmentIdOrderByStartDateTimeDesc(UUID departmentId);
    List<Event> findByCreatedByIdOrderByStartDateTimeDesc(UUID createdById);
    List<Event> findByParentEventIdOrderByStartDateTimeDesc(UUID parentEventId);
    List<Event> findByCollegeIdOrEventLevelOrderByStartDateTimeDesc(UUID collegeId, EventLevel eventLevel);
}
