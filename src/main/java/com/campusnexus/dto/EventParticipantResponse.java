package com.campusnexus.dto;

import com.campusnexus.enums.PaymentStatus;
import com.campusnexus.enums.TicketStatus;
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
public class EventParticipantResponse {
    private UUID registrationId;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private String ticketCode;
    private TicketStatus ticketStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime registeredAt;
}
