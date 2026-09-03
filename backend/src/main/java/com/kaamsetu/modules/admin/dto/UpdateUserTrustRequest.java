package com.kaamsetu.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserTrustRequest {

    @NotBlank(message = "Trust status is required")
    private String trustStatus; // 'HEALTHY' | 'WARNING' | 'RESTRICTED' | 'SUSPENDED' | 'BANNED'

    private String reason;
}
