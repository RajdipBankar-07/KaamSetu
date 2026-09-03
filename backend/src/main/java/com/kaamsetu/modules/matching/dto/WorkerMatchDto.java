package com.kaamsetu.modules.matching.dto;

import com.kaamsetu.modules.worker.entity.WorkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerMatchDto {
    private WorkerEntity worker;
    private double distanceKm;
    private int matchPercentage;
    private boolean availableToday;
    private boolean skillMatched;
    private boolean highTrust;
}
