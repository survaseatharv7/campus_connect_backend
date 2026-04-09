package com.campusnexus.dto;

import com.campusnexus.enums.SubmissionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {

    @NotNull(message = "Submission type is required")
    private SubmissionType submissionType;

    private String fileUrl;

    private String description;

    private String teamName;

    private List<UUID> teamMemberIds;
}
