package com.campusnexus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileRequest {
    private String name;
    private String phone;
    private String profilePicUrl;

    @Min(1) @Max(4)
    private Integer year;

    @Min(1) @Max(8)
    private Integer semester;

    private String rollNumber;
    private String division;
    private List<String> skills;
    private List<String> interests;
    private String bio;

    @URL
    private String githubUrl;
    @URL
    private String linkedinUrl;
    @URL
    private String portfolioUrl;
    @URL
    private String resumeUrl;
}
