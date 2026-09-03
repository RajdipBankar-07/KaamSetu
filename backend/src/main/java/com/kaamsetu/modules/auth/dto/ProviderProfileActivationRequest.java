package com.kaamsetu.modules.auth.dto;

import com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfileActivationRequest {
    private String businessName;
    private ProviderTypeEnum providerType;
}
