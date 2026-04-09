package com.campusnexus.dto;

import com.campusnexus.enums.BroadcastLevel;
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
public class BroadcastResponse {
    private UUID id;
    private String title;
    private String message;
    private String attachmentUrl;
    private BroadcastLevel level;
    private String levelLabel;
    private String senderName;
    private String senderRole;
    private LocalDateTime sentAt;
}
