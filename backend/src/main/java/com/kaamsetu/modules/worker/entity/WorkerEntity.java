package com.kaamsetu.modules.worker.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "workers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

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

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String state = "Maharashtra";

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String country = "India";

    @Column(length = 10)
    private String pincode;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "travel_radius_km", nullable = false)
    @Builder.Default
    private Integer travelRadiusKm = 10;

    @Column(name = "min_daily_wage", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minDailyWage = new BigDecimal("500.00");

    @Column(name = "experience_years", nullable = false)
    @Builder.Default
    private Integer experienceYears = 1;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = new BigDecimal("5.00");

    @Column(name = "trust_index", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal trustIndex = new BigDecimal("5.00");

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "availability_days", columnDefinition = "TEXT")
    private String availabilityDays;

    @Column(name = "is_available_today", nullable = false)
    @Builder.Default
    private boolean availableToday = true;

    @Transient
    private String mobile;

    @Transient
    private String email;

    @Transient
    private String username;

    @Transient
    private String gender;
}
