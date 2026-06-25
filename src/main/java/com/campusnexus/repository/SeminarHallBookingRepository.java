package com.campusnexus.repository;

import com.campusnexus.entity.SeminarHallBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeminarHallBookingRepository extends JpaRepository<SeminarHallBooking, UUID> {
    List<SeminarHallBooking> findByHallId(UUID hallId);
    List<SeminarHallBooking> findByBookedById(UUID bookedById);
}
