package com.kaamsetu.modules.provider.entity;

import com.kaamsetu.core.common.BaseEntity;
import com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "providers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "business_name", length = 200)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 30)
    @Builder.Default
    private ProviderTypeEnum providerType = ProviderTypeEnum.FARMER;

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

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = new BigDecimal("5.00");

    @Column(name = "payment_reliability_score", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal paymentReliabilityScore = new BigDecimal("5.00");

    @Column(name = "trust_index", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal trustIndex = new BigDecimal("5.00");

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Transient
    private String mobile;

    @Transient
    private String email;

    @Transient
    private String username;

    @Transient
    private String gender;
}
