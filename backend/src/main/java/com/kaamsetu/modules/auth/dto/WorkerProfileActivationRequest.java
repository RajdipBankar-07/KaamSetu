package com.kaamsetu.modules.auth.dto;

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
public class WorkerProfileActivationRequest {
    private String skills;
    private List<String> skillsList;
    private BigDecimal minDailyWage;
    private Integer travelRadiusKm;
    private Integer experienceYears;
    private String bio;
}
