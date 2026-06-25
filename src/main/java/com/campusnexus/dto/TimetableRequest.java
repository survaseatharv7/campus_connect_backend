package com.campusnexus.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableRequest {

    private UUID departmentId;

    @NotNull(message = "Teacher ID is required")
    private UUID teacherId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Year is required")
    private String year;

    @NotNull(message = "Semester is required")
    private int semester;

    @NotBlank(message = "Division is required")
    private String division;

    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    @NotBlank(message = "Start time is required")
    @JsonProperty("startTime")
    @JsonAlias({"startTime", "fromTime"})
    private String startTime;

    @NotBlank(message = "End time is required")
    @JsonProperty("endTime")
    @JsonAlias({"endTime", "toTime"})
    private String endTime;
}

