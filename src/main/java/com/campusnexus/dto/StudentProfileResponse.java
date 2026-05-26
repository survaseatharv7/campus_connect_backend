package com.campusnexus.dto;

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
public class StudentProfileResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String profilePicUrl;

    private Integer year;
    private Integer semester;
    private String rollNumber;
    private String division;
    private List<String> skills;
    private List<String> interests;
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String portfolioUrl;
    private String resumeUrl;
    private java.time.LocalDateTime updatedAt;
}
