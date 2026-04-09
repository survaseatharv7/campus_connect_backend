package com.campusnexus.dto;

import com.campusnexus.enums.CollegeStatus;
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
public class CollegeResponse {
    private UUID id;
    private String uniqueCollegeCode;
    private String name;
    private String address;
    private String city;
    private String state;
    private String logoUrl;
    private CollegeStatus status;
    private String principalName;
    private LocalDateTime createdAt;
}
