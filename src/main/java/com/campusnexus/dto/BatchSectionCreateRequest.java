package com.campusnexus.dto;

import com.campusnexus.enums.BatchSectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSectionCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Section type is required")
    private BatchSectionType sectionType;

    private LocalDate deadlineDate;

    private LocalDateTime deadline;

    private Boolean isActive;
}
