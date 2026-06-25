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
public class StudentProgressRequest {

    @NotBlank(message = "Progress note is required")
    private String progressNote;

    @NotNull(message = "Percentage is required")
    private Integer percentage;

    @NotBlank(message = "Subject is required")
    private String subject;
}
