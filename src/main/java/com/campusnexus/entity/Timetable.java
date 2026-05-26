package com.campusnexus.entity;

import com.campusnexus.enums.TimetableStatus;
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
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(name = "from_time", nullable = false)
    private String startTime;

    @Column(name = "to_time", nullable = false)
    private String endTime;

    @Column(nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private String room;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false)
    private String division;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TimetableStatus status = TimetableStatus.PUBLISHED;

    private String teacherName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
