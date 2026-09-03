package com.kaamsetu.modules.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkerProfileRequest {
    private String fullName;
    private String mobile;
    private String email;
    private String countryId;
    private String stateId;
    private String districtId;
    private String talukaId;
    private String villageId;
    private String country;
    private String state;
    private String district;
    private String taluka;
    private String village;
    private String pincode;
    private Integer travelRadiusKm;
    private BigDecimal minDailyWage;
    private Integer experienceYears;
    private String bio;
    private String avatarUrl;
    private Boolean availableToday;
    private String gender;
    private String skills;
    private String availabilityDays;
}
