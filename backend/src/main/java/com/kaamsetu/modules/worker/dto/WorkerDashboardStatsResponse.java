package com.kaamsetu.modules.worker.dto;

import com.kaamsetu.modules.review.dto.PendingRatingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDashboardStatsResponse {

    private long availableJobsCount;
    private long myApplicationsCount;
    private long completedJobsCount;
    private BigDecimal minDailyWage;
    private BigDecimal trustIndex;
    private BigDecimal averageRating;
    private long ratingsCount;
    private List<PendingRatingDto> pendingRatings;
}
