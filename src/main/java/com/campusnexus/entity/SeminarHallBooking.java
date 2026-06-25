package com.campusnexus.entity;

import com.campusnexus.enums.SeminarHallStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seminar_hall_bookings")
public class SeminarHallBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private SeminarHall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by", nullable = false)
    private User bookedBy;

    @Column(nullable = false)
    private String eventOrPurpose;

    @Column(nullable = false)
    private LocalDateTime fromDateTime;

    @Column(nullable = false)
    private LocalDateTime toDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeminarHallStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
