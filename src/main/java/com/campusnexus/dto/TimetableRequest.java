package com.campusnexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableRequest {

    @NotNull(message = "Batch ID is required")
    private UUID batchId;

    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime fromTime;

    @NotNull(message = "End time is required")
    private LocalTime toTime;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotNull(message = "Teacher ID is required")
    private UUID teacherId;

    private String room;
}
