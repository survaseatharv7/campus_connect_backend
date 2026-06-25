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
public class ExternalRegistrationResponse {
    private UUID id;
    private UUID eventId;
    private String eventTitle;
    private String guestName;
    private String guestEmail;
    private String collegeName;
    private String city;
    private TicketStatus ticketStatus;
    private PaymentStatus paymentStatus;
    private String stripeClientSecret; // null for free events
    private LocalDateTime registeredAt;
}
