package com.campusnexus.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AISuggestRequest {

    @NotBlank(message = "Year is required")
    private String year;

    @NotNull(message = "Semester is required")
    private int semester;

    @NotBlank(message = "Division is required")
    private String division;

    @NotNull(message = "Teacher-subject mappings are required")
    private List<TeacherSubjectMapping> teacherSubjectMappings;

    @NotNull(message = "Working days are required")
    private List<String> workingDays;

    @NotNull(message = "Time slots are required")
    private List<String> timeSlots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherSubjectMapping {
        @NotNull(message = "Teacher ID is required")
        private UUID teacherId;

        @NotBlank(message = "Teacher name is required")
        private String teacherName;

        @NotBlank(message = "Subject is required")
        private String subject;

        @NotNull(message = "Lectures per week is required")
        private int lecturesPerWeek;
    }
}
