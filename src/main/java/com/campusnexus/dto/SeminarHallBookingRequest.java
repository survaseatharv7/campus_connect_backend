package com.campusnexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SeminarHallBookingRequest {

    @NotNull(message = "Hall ID is required")
    private UUID hallId;

    @NotBlank(message = "Event or purpose is required")
    private String eventOrPurpose;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime fromDateTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime toDateTime;
}
