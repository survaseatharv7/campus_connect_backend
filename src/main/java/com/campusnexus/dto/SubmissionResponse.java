package com.campusnexus.dto;

import com.campusnexus.enums.SubmissionStatus;
import com.campusnexus.enums.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private UUID id;
    private String studentName;
    private String teamName;
    private List<String> teamMembers;
    private List<String> teamMemberNames;
    private Integer teamSize;
    private SubmissionType submissionType;
    private String fileUrl;
    private String description;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private String professorRemark;
    private LocalDateTime remarkAt;
    private String reviewedByName;
}
