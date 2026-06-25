package com.campusnexus.dto;

import com.campusnexus.enums.SubmissionStatus;
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
public class SubmissionRemarkRequest {

    @NotBlank(message = "Remark is required")
    private String remark;

    @NotNull(message = "Status is required")
    private SubmissionStatus status;
}
