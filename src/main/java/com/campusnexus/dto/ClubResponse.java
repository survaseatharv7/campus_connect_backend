package com.campusnexus.dto;

import com.campusnexus.enums.ClubStatus;
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
public class ClubResponse {
    private UUID id;
    private String name;
    private String description;
    private String logoUrl;
    private ClubStatus status;
    private String statusLabel;
    private String guideName;
    private Integer memberCount;
    private String createdByName;
    private LocalDateTime createdAt;
    private Boolean isMember;
    private Boolean isOwner;
}
