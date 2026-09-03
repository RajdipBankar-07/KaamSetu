package com.kaamsetu.modules.provider.dto;

import com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProviderProfileRequest {
    private String name;
    private String fullName;
    private String businessName;
    private String mobile;
    private String email;
    private ProviderTypeEnum providerType;
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
    private String bio;
    private String gender;
}
