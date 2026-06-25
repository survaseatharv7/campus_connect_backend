package com.campusnexus.dto;

import com.campusnexus.enums.SeminarHallType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarHallResponse {
    private UUID id;
    private String name;
    private Integer capacity;
    private String location;
    private String amenities;
    private SeminarHallType hallType;
    private String collegeName;
    private String departmentName;
    private Boolean isActive;
}
