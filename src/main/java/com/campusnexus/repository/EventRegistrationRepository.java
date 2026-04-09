package com.campusnexus.repository;

import com.campusnexus.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    List<EventRegistration> findByStudentId(UUID studentId);
    List<EventRegistration> findByEventId(UUID eventId);
    Optional<EventRegistration> findByEventIdAndStudentId(UUID eventId, UUID studentId);
    Optional<EventRegistration> findByStripePaymentIntentId(String paymentIntentId);
    boolean existsByEventIdAndStudentId(UUID eventId, UUID studentId);
    long countByEventId(UUID eventId);
}
