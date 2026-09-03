package com.kaamsetu.modules.provider.dto;

import com.kaamsetu.modules.job.entity.JobEntity;
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
public class ProviderDashboardStatsResponse {

    private long postedJobsCount;
    private long totalApplicationsCount;
    private long confirmedWorkersCount;
    private BigDecimal averageRating;
    private long ratingsCount;
    private BigDecimal trustIndex;
    private List<JobEntity> recentJobs;
    private List<PendingRatingDto> pendingRatings;
}
