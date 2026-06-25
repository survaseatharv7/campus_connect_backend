package com.campusnexus.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.enums.EventType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "events")
@JsonIgnoreProperties({ "registrations", "createdBy", "college", "department", "club", "parentEvent" })
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String posterUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventLevel eventLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    @JsonIgnore
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    @JsonIgnore
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event_id")
    @JsonIgnore
    private Event parentEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private String venue;

    private Integer maxParticipants;

    @Column(nullable = false)
    @Builder.Default
    private Integer registeredCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Column(nullable = true)
    @Builder.Default
    private boolean openToExternal = false;

    @Column(precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EventRegistration> registrations;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = EventStatus.UPCOMING;
        }
        if (registeredCount == null) {
            registeredCount = 0;
        }
        if (isPaid == null) {
            isPaid = false;
        }
    }
}
