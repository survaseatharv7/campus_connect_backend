package com.campusnexus.dto;

import com.campusnexus.enums.TimetableStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimetableResponse {
    private UUID id;
    private UUID departmentId;
    private UUID teacherId;
    private String teacherName;
    private String subject;
    private String year;
    private int semester;
    private String division;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private TimetableStatus status;
    private LocalDateTime createdAt;

    // For conflict detection in AI suggestions
    private boolean hasConflict;
    private String conflictReason;

    // "TEACHING" for timetable slots, "AVAILABILITY" for merged availability view
    @Builder.Default
    private String type = "TEACHING";

    public String getFromTime() {
        return startTime;
    }

    public String getToTime() {
        return endTime;
    }
}

