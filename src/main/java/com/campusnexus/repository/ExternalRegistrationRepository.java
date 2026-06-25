package com.campusnexus.repository;

import com.campusnexus.entity.ExternalRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExternalRegistrationRepository extends JpaRepository<ExternalRegistration, UUID> {
    List<ExternalRegistration> findByEvent_Id(UUID eventId);

    boolean existsByEvent_IdAndGuestEmail(UUID eventId, String guestEmail);

    List<ExternalRegistration> findByGuestEmail(String guestEmail);

    Optional<ExternalRegistration> findByStripePaymentIntentId(String stripePaymentIntentId);
}
