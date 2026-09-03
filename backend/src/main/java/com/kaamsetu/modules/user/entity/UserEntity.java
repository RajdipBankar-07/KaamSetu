package com.kaamsetu.modules.user.entity;

import com.kaamsetu.core.common.BaseEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), status = 'DEACTIVATED' WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoleEnum role = RoleEnum.WORKER;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private GenderEnum gender = GenderEnum.MALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_preference", nullable = false, length = 5)
    @Builder.Default
    private LanguageCodeEnum languagePreference = LanguageCodeEnum.mr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatusEnum status = UserStatusEnum.PENDING;

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

    @Column(length = 100)
    @Builder.Default
    private String country = "India";

    @Column(length = 100)
    @Builder.Default
    private String state = "Maharashtra";

    @Column(length = 100)
    @Builder.Default
    private String district = "Pune Rural";

    @Column(length = 100)
    @Builder.Default
    private String taluka = "Shirur";

    @Column(length = 100)
    private String village;

    @Column(name = "is_mobile_verified", nullable = false)
    @Builder.Default
    private boolean mobileVerified = false;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "is_location_verified", nullable = false)
    @Builder.Default
    private boolean locationVerified = false;

    @Column(name = "is_identity_verified", nullable = false)
    @Builder.Default
    private boolean identityVerified = false;

    @Transient
    @Builder.Default
    private String trustStatus = "HEALTHY";

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void setIsVerified(boolean verified) {
        this.identityVerified = verified;
        this.mobileVerified = verified;
        this.emailVerified = verified;
    }
}
