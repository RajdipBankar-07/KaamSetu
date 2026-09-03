package com.kaamsetu.modules.auth.dto;

import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Builder.Default
    private GenderEnum gender = GenderEnum.MALE;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^(\\+91)?[0-9]{10}$", message = "Invalid Indian mobile number format")
    private String mobile;

    @Builder.Default
    private String countryId = "IN";

    @Builder.Default
    private String stateId = "state-mh";

    @Builder.Default
    private String districtId = "dist-pune";

    @Builder.Default
    private String talukaId = "tal-shirur";

    private String villageId;

    @Builder.Default
    private String country = "India";

    @Builder.Default
    private String state = "Maharashtra";

    @Builder.Default
    private String district = "Pune Rural";

    @Builder.Default
    private String taluka = "Shirur";

    @NotBlank(message = "Village is required")
    private String village;

    @NotNull(message = "Role is required")
    private RoleEnum role;

    @Builder.Default
    private LanguageCodeEnum languagePreference = LanguageCodeEnum.mr;

    // Optional verification overrides from registration steps
    private boolean mobileVerified;
    private boolean emailVerified;

    // Optional initial worker preferences
    private String category;
    private Double minDailyWage;
}
