package com.kaamsetu.modules.matching.dto;

import com.kaamsetu.modules.job.entity.JobEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchDto {
    private JobEntity job;
    private double distanceKm;
    private int matchPercentage; // 0 - 100%
    private boolean categoryMatched;
    private boolean withinRadius;
    private boolean wageSatisfied;
    private boolean isUrgent;
}
