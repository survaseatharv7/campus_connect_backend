package com.campusnexus.dto;

import com.campusnexus.enums.TeacherAvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAvailabilityRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime fromTime;

    @NotNull(message = "End time is required")
    private LocalTime toTime;

    @NotNull(message = "Status is required")
    private TeacherAvailabilityStatus status;

    private String note;
}
