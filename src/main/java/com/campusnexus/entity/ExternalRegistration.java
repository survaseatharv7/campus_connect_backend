package com.campusnexus.entity;

import com.campusnexus.enums.PaymentStatus;
import com.campusnexus.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "external_registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String guestEmail;

    @Column(nullable = false)
    private String collegeName;

    @Column(nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // For Stripe paid events
    private String stripePaymentIntentId;
    private String stripeClientSecret;

    @CreationTimestamp
    private LocalDateTime registeredAt;
}
