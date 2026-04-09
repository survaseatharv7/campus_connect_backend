package com.campusnexus.dto;

import com.campusnexus.enums.SeminarHallType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarHallRequest {

    @NotBlank(message = "Hall name is required")
    private String name;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    private String location;

    private String amenities;

    @NotNull(message = "Hall type is required")
    private SeminarHallType hallType;

    // No @NotNull — collegeId is injected server-side from authenticated user
    private UUID collegeId;

    private UUID departmentId;
}