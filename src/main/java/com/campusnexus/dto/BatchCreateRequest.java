package com.campusnexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchCreateRequest {

    @NotBlank(message = "Batch name is required")
    private String batchName;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Semester is required")
    private Integer semester;
}
