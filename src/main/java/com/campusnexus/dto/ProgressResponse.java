package com.campusnexus.dto;

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
public class ProgressResponse {
    private UUID id;
    private String studentName;
    private String updatedByName;
    private String progressNote;
    private Integer percentage;
    private String subject;
    private LocalDateTime updatedAt;
}
