package com.kaamsetu.modules.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "Taluka is required")
    private String taluka;

    @Builder.Default
    private String district = "Pune Rural";

    @Builder.Default
    private String countryId = "IN";

    @Builder.Default
    private String stateId = "state-mh";

    @Builder.Default
    private String districtId = "dist-pune";

    @Builder.Default
    private String talukaId = "tal-shirur";

    private String villageId;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Daily wage is required")
    @DecimalMin(value = "100.00", message = "Minimum daily wage is 100")
    private BigDecimal dailyWage;

    @NotNull(message = "Workers required count is mandatory")
    @Min(value = 1, message = "At least 1 worker is required")
    @Builder.Default
    private Integer workersRequired = 1;

    @Builder.Default
    private String workModel = "ONETIME"; // 'ONETIME' | 'DAILY' | 'RECURRING'

    private LocalDate deadline;

    @Builder.Default
    private String paymentUnit = "PER_DAY"; // 'PER_DAY' | 'PER_HOUR' | 'PER_MONTH' | 'TOTAL'

    @Builder.Default
    private Boolean overtimeAvailable = false;

    private BigDecimal overtimeRate;

    private String additionalPaymentConditions;

    private String startTime;
    private String endTime;

    @Builder.Default
    private BigDecimal workingHours = new BigDecimal("8.00");

    private String lunchBreak;
    private String teaBreak;
    private String otherBreak;

    private String facilities;
    private String facilityDetails;

    @Builder.Default
    private String priority = "NORMAL"; // 'NORMAL' | 'URGENT'

    @Builder.Default
    private Boolean isRecurring = false;
    private String recurrenceSchedule;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Builder.Default
    private Integer durationDays = 1;
}
