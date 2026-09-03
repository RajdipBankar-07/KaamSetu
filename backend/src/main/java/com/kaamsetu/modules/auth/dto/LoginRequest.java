package com.kaamsetu.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String username;

    private String mobile;

    @NotBlank(message = "Password is required")
    private String password;

    public String getIdentifier() {
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        return mobile != null ? mobile.trim() : "";
    }
}
