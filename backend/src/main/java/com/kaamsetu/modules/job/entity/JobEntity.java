package com.kaamsetu.modules.job.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobEntity extends BaseEntity {

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "country_id", length = 50)
    @Builder.Default
    private String countryId = "IN";

    @Column(name = "state_id", length = 50)
    @Builder.Default
    private String stateId = "state-mh";

    @Column(name = "district_id", length = 50)
    @Builder.Default
    private String districtId = "dist-pune";

    @Column(name = "taluka_id", length = 50)
    @Builder.Default
    private String talukaId = "tal-shirur";

    @Column(name = "village_id", length = 50)
    private String villageId;

    @Column(nullable = false, length = 100)
    private String village;

    @Column(nullable = false, length = 100)
    private String taluka;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String district = "Pune Rural";

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "daily_wage", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWage;

    @Column(name = "workers_required", nullable = false)
    @Builder.Default
    private Integer workersRequired = 1;

    @Column(name = "workers_confirmed", nullable = false)
    @Builder.Default
    private Integer workersConfirmed = 0;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String priority = "NORMAL"; // 'NORMAL' | 'URGENT'

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "OPEN"; // 'DRAFT' | 'OPEN' | 'FILLED' | 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED' | 'CANCELLED' | 'CLOSED'

    @Column(name = "work_model", length = 30)
    @Builder.Default
    private String workModel = "ONETIME"; // 'ONETIME' | 'DAILY' | 'RECURRING'

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "payment_unit", length = 30)
    @Builder.Default
    private String paymentUnit = "PER_DAY"; // 'PER_DAY' | 'PER_HOUR' | 'PER_MONTH' | 'TOTAL'

    @Column(name = "overtime_available", nullable = false)
    @Builder.Default
    private Boolean overtimeAvailable = false;

    @Column(name = "overtime_rate", precision = 10, scale = 2)
    private BigDecimal overtimeRate;

    @Column(name = "additional_payment_conditions", columnDefinition = "TEXT")
    private String additionalPaymentConditions;

    @Column(name = "start_time", length = 30)
    private String startTime;

    @Column(name = "end_time", length = 30)
    private String endTime;

    @Column(name = "working_hours", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal workingHours = new BigDecimal("8.00");

    @Column(name = "lunch_break", length = 100)
    private String lunchBreak;

    @Column(name = "tea_break", length = 100)
    private String teaBreak;

    @Column(name = "other_break", length = 100)
    private String otherBreak;

    @Column(columnDefinition = "TEXT")
    private String facilities;

    @Column(name = "facility_details", columnDefinition = "TEXT")
    private String facilityDetails;

    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "recurrence_schedule", length = 200)
    private String recurrenceSchedule;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "duration_days", nullable = false)
    @Builder.Default
    private Integer durationDays = 1;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "closed_at")
    private Instant closedAt;
}
