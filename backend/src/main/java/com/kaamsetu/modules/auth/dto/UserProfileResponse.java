package com.kaamsetu.modules.auth.dto;

import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String username;
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
    private RoleEnum role;
    private String activeRole;
    private List<String> availableRoles;
    private boolean hasWorkerProfile;
    private boolean hasProviderProfile;

    // Worker profile specifics
    private BigDecimal minDailyWage;
    private Integer travelRadiusKm;
    private Integer experienceYears;
    private String bio;
    private String skills;

    // Provider profile specifics
    private String businessName;
    private String providerType;

    private GenderEnum gender;
    private LanguageCodeEnum languagePreference;
    private UserStatusEnum status;
    private BigDecimal ratingAvg;
    private BigDecimal trustIndex;
    private boolean isMobileVerified;
    private boolean isEmailVerified;
    private boolean isLocationVerified;
    private boolean isIdentityVerified;

    public static UserProfileResponse fromEntity(UserEntity user) {
        return fromEntity(user, null, null, null);
    }

    public static UserProfileResponse fromEntity(UserEntity user, WorkerEntity worker) {
        return fromEntity(user, worker, null, null);
    }

    public static UserProfileResponse fromEntity(UserEntity user, WorkerEntity worker, ProviderEntity provider, String requestedActiveRole) {
        List<String> roles = new ArrayList<>();
        boolean isWorker = worker != null;
        boolean isProvider = provider != null;

        if (user.getRole() == RoleEnum.ADMIN) {
            roles.add("ADMIN");
        }
        if (isWorker) {
            roles.add("WORKER");
        }
        if (isProvider) {
            roles.add("PROVIDER");
        }
        if (roles.isEmpty()) {
            roles.add(user.getRole().name());
        }

        String activeRole = requestedActiveRole;
        if (activeRole == null || !roles.contains(activeRole)) {
            if (user.getRole() == RoleEnum.ADMIN) {
                activeRole = "ADMIN";
            } else if (isWorker && user.getRole() == RoleEnum.WORKER) {
                activeRole = "WORKER";
            } else if (isProvider && user.getRole() == RoleEnum.PROVIDER) {
                activeRole = "PROVIDER";
            } else {
                activeRole = roles.get(0);
            }
        }

        UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .countryId(user.getCountryId())
                .stateId(user.getStateId())
                .districtId(user.getDistrictId())
                .talukaId(user.getTalukaId())
                .villageId(user.getVillageId())
                .country(user.getCountry())
                .state(user.getState())
                .district(user.getDistrict())
                .village(user.getVillage())
                .role(user.getRole())
                .activeRole(activeRole)
                .availableRoles(roles)
                .hasWorkerProfile(isWorker)
                .hasProviderProfile(isProvider)
                .gender(user.getGender())
                .languagePreference(user.getLanguagePreference())
                .status(user.getStatus())
                .isMobileVerified(user.isMobileVerified())
                .isEmailVerified(user.isEmailVerified())
                .isLocationVerified(user.isLocationVerified())
                .isIdentityVerified(user.isIdentityVerified());

        if (worker != null) {
            builder.minDailyWage(worker.getMinDailyWage())
                    .travelRadiusKm(worker.getTravelRadiusKm())
                    .experienceYears(worker.getExperienceYears())
                    .bio(worker.getBio())
                    .skills(worker.getSkills())
                    .ratingAvg(worker.getRatingAvg())
                    .trustIndex(worker.getTrustIndex());
            if (worker.getTaluka() != null) builder.taluka(worker.getTaluka());
            if (worker.getVillage() != null) builder.village(worker.getVillage());
            if (worker.getDistrict() != null) builder.district(worker.getDistrict());
            if (worker.getState() != null) builder.state(worker.getState());
            if (worker.getCountry() != null) builder.country(worker.getCountry());
        }

        if (provider != null) {
            builder.businessName(provider.getBusinessName())
                    .providerType(provider.getProviderType() != null ? provider.getProviderType().name() : null);
            if (worker == null) {
                builder.ratingAvg(provider.getRatingAvg())
                        .trustIndex(provider.getTrustIndex());
                if (provider.getTaluka() != null) builder.taluka(provider.getTaluka());
                if (provider.getVillage() != null) builder.village(provider.getVillage());
                if (provider.getDistrict() != null) builder.district(provider.getDistrict());
                if (provider.getState() != null) builder.state(provider.getState());
                if (provider.getCountry() != null) builder.country(provider.getCountry());
            }
        }

        return builder.build();
    }
}
