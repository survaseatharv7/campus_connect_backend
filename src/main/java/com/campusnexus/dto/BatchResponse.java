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
public class BatchResponse {
    private UUID id;
    private String batchName;
    private Integer year;
    private Integer semester;
    private String departmentName;
    private String professorName;
    private Integer sectionCount;
    private LocalDateTime createdAt;
}
