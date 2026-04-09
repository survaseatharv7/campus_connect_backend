package com.campusnexus.dto;

import com.campusnexus.enums.EventLevel;
import com.campusnexus.enums.EventStatus;
import com.campusnexus.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private String posterUrl;
    private EventLevel eventLevel;
    private EventType eventType;
    private EventStatus status;
    private String createdByName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String venue;
    private Integer maxParticipants;
    private Integer registeredCount;
    private Integer participantCount;
    private Boolean isPaid;
    private BigDecimal ticketPrice;
    private LocalDateTime createdAt;
    private Boolean isRegistered;
    private List<EventResponse> subEvents;
}
