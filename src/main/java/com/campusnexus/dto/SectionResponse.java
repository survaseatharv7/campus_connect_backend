package com.campusnexus.dto;

import com.campusnexus.enums.BatchSectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    private UUID id;
    private String title;
    private String description;
    private BatchSectionType sectionType;
    private LocalDate deadlineDate;
    private Boolean isActive;
    private Integer submissionCount;
    private String batchName;
    private String teacherName;
    private LocalDateTime deadline;
}
