package com.campusnexus.dto;

import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event level is required")
    private EventLevel eventLevel;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @Builder.Default
    private Boolean isPaid = false;

    @Builder.Default
    private boolean openToExternal = false;

    @DecimalMin(value = "0.00", inclusive = true, message = "Ticket price cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Ticket price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal ticketPrice;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;

    private String venue;

    private Integer maxParticipants;

    private UUID parentEventId;

    private UUID clubId;

    private UUID collegeId;

    private UUID departmentId;

    private String posterUrl;
}
