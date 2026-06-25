package com.campusnexus.entity;

import com.campusnexus.enums.CollegeStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "colleges")
@JsonIgnoreProperties({"principal", "createdBy"})
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private String uniqueCollegeCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;

    private String state;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollegeStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principal_id")
    private User principal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
