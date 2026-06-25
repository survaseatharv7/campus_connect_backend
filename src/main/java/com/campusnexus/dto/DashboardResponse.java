package com.campusnexus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalColleges;
    private long totalUsers;
    private long totalEvents;
    private long totalRegistrations;
    private long totalClubs;
    private long activeStudents;
}
