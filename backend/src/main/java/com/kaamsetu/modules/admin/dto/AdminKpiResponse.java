package com.kaamsetu.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminKpiResponse {
    private long totalUsers;
    private long activeWorkers;
    private long activeProviders;
    private long openJobs;
    private long filledJobs;
    private long completedJobs;
    private long pendingReports;
    private double completionRate; // Percentage e.g. 94.2%
    private double avgFillTimeHours; // e.g. 3.5 hours
    private double noShowRate; // Percentage e.g. 1.2%
    private double repeatHiringRate; // Percentage e.g. 68.4%
}
