package com.campusnexus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableResponse {
    private UUID id;
    private String dayOfWeek;
    private LocalTime fromTime;
    private LocalTime toTime;
    private String subject;
    private String teacherName;
    private String batchName;
    private String room;
}
