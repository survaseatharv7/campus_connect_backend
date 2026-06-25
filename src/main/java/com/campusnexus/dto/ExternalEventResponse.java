package com.campusnexus.dto;

import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventStatus;
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
public class ExternalEventResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String venue;
    private EventLevel eventLevel;
    private BigDecimal ticketPrice;
    private Integer maxParticipants;
    private String posterUrl;
    private EventStatus status;
}
