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
public class NotesResponse {
    private UUID id;
    private String title;
    private String description;
    private String fileUrl;
    private String subject;
    private Integer year;
    private Integer semester;
    private String uploaderName;
    private String departmentName;
    private LocalDateTime uploadedAt;
}
