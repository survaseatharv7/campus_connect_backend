package com.campusnexus.dto;

import com.campusnexus.enums.TeacherAvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private UUID id;
    private String teacherName;
    private LocalDate date;
    private LocalTime fromTime;
    private LocalTime toTime;
    private TeacherAvailabilityStatus status;
    private String note;
}
