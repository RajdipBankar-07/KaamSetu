package com.kaamsetu.modules.auth.service;

import com.kaamsetu.core.security.EmailVerificationService;
import com.kaamsetu.core.security.JwtTokenProvider;
import com.kaamsetu.core.security.OtpService;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.admin.entity.AuditLogEntity;
import com.kaamsetu.modules.admin.repository.AuditLogRepository;
import com.kaamsetu.modules.auth.dto.*;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import com.kaamsetu.modules.location.service.LocationHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final AuditLogRepository auditLogRepository;
    private final OtpService otpService;
    private final EmailVerificationService emailVerificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final LocationHierarchyService locationHierarchyService;

    @Value("${kaamsetu.jwt.expiration-ms:604800000}")
    private long jwtExpirationMs;

    private static final List<String> RESERVED_USERNAMES = List.of(
            "admin", "administrator", "system", "root", "superuser", "kaamsetu", "mod", "moderator"
    );

    /**
     * User Registration with Mobile OTP, Email Verification, and Admin Approval Flow.
     * New users start in PENDING status until reviewed and APPROVED by an Admin.
     */
    public static String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    public static String normalizeMobile(String mobile) {
        if (mobile == null) return "";
        String digits = mobile.replaceAll("[^0-9]", "");
        if (digits.length() >= 10) {
            return "+91" + digits.substring(digits.length() - 10);
        }
        return mobile.trim();
    }

    public boolean isMobileRegistered(String mobile) {
        if (mobile == null || mobile.isBlank()) return false;
        String normalized = normalizeMobile(mobile);
        String rawDigits = mobile.replaceAll("[^0-9]", "");
        String tenDigits = rawDigits.length() >= 10 ? rawDigits.substring(rawDigits.length() - 10) : rawDigits;

        return userRepository.existsByMobile(mobile.trim())
                || userRepository.existsByMobile(normalized)
                || (!tenDigits.isEmpty() && (userRepository.existsByMobile(tenDigits) || userRepository.existsByMobile("0" + tenDigits) || userRepository.existsByMobile("+91 " + tenDigits)));
    }

    /**
     * User Registration with Mobile OTP, Email Verification, and Admin Approval Flow.
     * New users start in PENDING status until reviewed and APPROVED by an Admin.
     */
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.getUsername());
        String email = normalizeEmail(request.getEmail());
        String mobile = normalizeMobile(request.getMobile());

        // 1. Role check
        if (request.getRole() == RoleEnum.ADMIN) {
            throw new IllegalArgumentException("Direct registration for ADMIN role is not permitted");
        }

        // 2. Validate reserved usernames
        if (RESERVED_USERNAMES.contains(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is reserved and cannot be registered");
        }

        // 3. Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        // 4. Duplicate checks (Rules 2, 3, 4)
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (isMobileRegistered(request.getMobile())) {
            throw new IllegalArgumentException("Mobile number already registered.");
        }
        if (!email.isEmpty() && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // 5. Hierarchical Location Validation
        if (locationHierarchyService != null) {
            locationHierarchyService.validateHierarchy(
                    request.getCountryId(),
                    request.getStateId(),
                    request.getDistrictId(),
                    request.getTalukaId(),
                    request.getVillageId()
            );
        }

        // 6. Create user with status PENDING
        UserEntity user = UserEntity.builder()
                .username(username)
                .fullName(request.getFullName().trim())
                .mobile(mobile.isEmpty() ? request.getMobile().trim() : mobile)
                .email(email.isEmpty() ? username + "@kaamsetu.org" : email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .gender(request.getGender() != null ? request.getGender() : GenderEnum.MALE)
                .languagePreference(request.getLanguagePreference() != null ? request.getLanguagePreference() : LanguageCodeEnum.mr)
                .status(UserStatusEnum.PENDING)
                .countryId(request.getCountryId() != null ? request.getCountryId() : "IN")
                .stateId(request.getStateId() != null ? request.getStateId() : "state-mh")
                .districtId(request.getDistrictId() != null ? request.getDistrictId() : "dist-pune")
                .talukaId(request.getTalukaId() != null ? request.getTalukaId() : "tal-shirur")
                .villageId(request.getVillageId())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .state(request.getState() != null ? request.getState() : "Maharashtra")
                .district(request.getDistrict() != null ? request.getDistrict() : "Pune Rural")
                .village(request.getVillage().trim())
                .mobileVerified(request.isMobileVerified())
                .emailVerified(request.isEmailVerified())
                .identityVerified(false)
                .locationVerified(false)
                .build();

        user = userRepository.save(user);

        // 7. Send Email Verification Token Link
        if (!user.isEmailVerified()) {
            emailVerificationService.createAndSendVerificationEmail(user.getId(), user.getEmail(), user.getFullName());
        }

        // 8. Create associated Worker or Provider profile entity
        WorkerEntity worker = null;
        ProviderEntity provider = null;

        if (user.getRole() == RoleEnum.WORKER) {
            worker = WorkerEntity.builder()
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .countryId(user.getCountryId())
                    .stateId(user.getStateId())
                    .districtId(user.getDistrictId())
                    .talukaId(user.getTalukaId())
                    .villageId(user.getVillageId())
                    .village(user.getVillage())
                    .taluka(request.getTaluka() != null ? request.getTaluka() : user.getVillage())
                    .district(user.getDistrict())
                    .state(user.getState())
                    .minDailyWage(request.getMinDailyWage() != null ? BigDecimal.valueOf(request.getMinDailyWage()) : new BigDecimal("500.00"))
                    .travelRadiusKm(10)
                    .experienceYears(1)
                    .build();
            worker = workerRepository.save(worker);
        } else if (user.getRole() == RoleEnum.PROVIDER) {
            provider = ProviderEntity.builder()
                    .userId(user.getId())
                    .name(user.getFullName())
                    .countryId(user.getCountryId())
                    .stateId(user.getStateId())
                    .districtId(user.getDistrictId())
                    .talukaId(user.getTalukaId())
                    .villageId(user.getVillageId())
                    .village(user.getVillage())
                    .taluka(request.getTaluka() != null ? request.getTaluka() : user.getVillage())
                    .district(user.getDistrict())
                    .state(user.getState())
                    .build();
            provider = providerRepository.save(provider);
        }

        // 9. Audit new pending registration
        auditLogRepository.save(AuditLogEntity.builder()
                .actorUserId(user.getId())
                .actionType("USER_REGISTERED_PENDING_APPROVAL")
                .entityName("users")
                .entityId(user.getId())
                .newState("{\"username\":\"" + username + "\",\"email\":\"" + user.getEmail() + "\",\"role\":\"" + user.getRole() + "\",\"status\":\"PENDING\"}")
                .build());

        log.info("New user registered with PENDING approval: {} ({})", username, user.getRole());
        return UserProfileResponse.fromEntity(user, worker, provider, user.getRole().name());
    }

    /**
     * Username + Password Login with Strict Verification & Account Status Decision Matrix
     */
    @Transactional
    public AuthResponse loginWithPassword(LoginRequest request) {
        String identifier = request.getIdentifier() != null ? request.getIdentifier().trim() : "";
        String normalizedIdentifier = identifier.toLowerCase();
        String normalizedMobile = normalizeMobile(identifier);

        // 1. Locate user by username, mobile, or email
        UserEntity user = userRepository.findByUsernameIgnoreCase(normalizedIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedIdentifier))
                .or(() -> userRepository.findByMobile(identifier))
                .or(() -> userRepository.findByMobile(normalizedMobile))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // 2. Validate password hash
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // 3. Enforce Verification & Account Status Lifecycle Matrix
        if (user.getStatus() == UserStatusEnum.PENDING) {
            throw new BadCredentialsException("Your account is waiting for administrator approval.");
        }
        if (user.getStatus() == UserStatusEnum.REJECTED) {
            throw new BadCredentialsException("Your registration request was rejected by administrator.");
        }
        if (user.getStatus() == UserStatusEnum.SUSPENDED) {
            throw new BadCredentialsException("Your account has been suspended. Please contact support.");
        }
        if (user.getStatus() == UserStatusEnum.BANNED) {
            throw new BadCredentialsException("Your account has been banned due to policy violations.");
        }
        if (user.getStatus() == UserStatusEnum.DEACTIVATED) {
            throw new BadCredentialsException("Your account has been deactivated.");
        }

        // 4. Update last login timestamp
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // 5. Generate secure JWT token pair
        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = jwtTokenProvider.generateTokenFromPrincipal(principal, jwtExpirationMs);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        // 6. Audit successful login
        auditLogRepository.save(AuditLogEntity.builder()
                .actorUserId(user.getId())
                .actionType("USER_LOGGED_IN_VIA_PASSWORD")
                .entityName("users")
                .entityId(user.getId())
                .build());

        WorkerEntity worker = workerRepository.findByUserId(user.getId()).orElse(null);
        ProviderEntity provider = providerRepository.findByUserId(user.getId()).orElse(null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(UserProfileResponse.fromEntity(user, worker, provider, null))
                .isNewUser(false)
                .build();
    }

    /**
     * Verify email via cryptographically secure single-use token
     */
    @Transactional
    public UserProfileResponse verifyEmail(String token) {
        UserEntity user = emailVerificationService.verifyEmailToken(token);
        return UserProfileResponse.fromEntity(user);
    }

    /**
     * Resend verification email
     */
    @Transactional
    public void resendEmailVerification(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email address is already verified");
        }

        emailVerificationService.createAndSendVerificationEmail(user.getId(), user.getEmail(), user.getFullName());
    }

    /**
     * Change Password with current password verification
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogRepository.save(AuditLogEntity.builder()
                .actorUserId(userId)
                .actionType("USER_CHANGED_PASSWORD")
                .entityName("users")
                .entityId(userId)
                .build());

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    public void sendOtp(OtpSendRequest request) {
        String mobile = request.getMobile();
        otpService.generateAndSendOtp(mobile);

        auditLogRepository.save(AuditLogEntity.builder()
                .actionType("OTP_SENT")
                .entityName("users")
                .newState("{\"mobile\":\"" + mobile + "\"}")
                .build());
    }

    public void sendEmailOtp(EmailOtpSendRequest request) {
        String email = request.getEmail();
        otpService.generateAndSendEmailOtp(email);

        auditLogRepository.save(AuditLogEntity.builder()
                .actionType("EMAIL_OTP_SENT")
                .entityName("users")
                .newState("{\"email\":\"" + email + "\"}")
                .build());
    }

    public boolean verifyEmailOtp(EmailOtpVerifyRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();

        boolean isValid = otpService.verifyEmailOtp(email, otp);
        if (!isValid) {
            throw new BadCredentialsException("Invalid or expired Email OTP");
        }

        auditLogRepository.save(AuditLogEntity.builder()
                .actionType("EMAIL_OTP_VERIFIED")
                .entityName("users")
                .newState("{\"email\":\"" + email + "\"}")
                .build());

        return true;
    }

    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String mobile = request.getMobile();
        String otp = request.getOtp();

        boolean isValid = otpService.verifyOtp(mobile, otp);
        if (!isValid) {
            throw new BadCredentialsException("Invalid or expired OTP");
        }

        Optional<UserEntity> existingUserOpt = userRepository.findByMobile(mobile);
        boolean isNewUser = existingUserOpt.isEmpty();
        UserEntity user;

        if (isNewUser) {
            RoleEnum initialRole = request.getPreferredRole() != null ? request.getPreferredRole() : RoleEnum.WORKER;
            LanguageCodeEnum initialLang = request.getLanguagePreference() != null ? request.getLanguagePreference() : LanguageCodeEnum.mr;

            user = UserEntity.builder()
                    .username("user_" + mobile.substring(Math.max(0, mobile.length() - 6)))
                    .fullName("User " + mobile.substring(Math.max(0, mobile.length() - 4)))
                    .mobile(mobile)
                    .email(mobile.substring(Math.max(0, mobile.length() - 6)) + "@kaamsetu.org")
                    .role(initialRole)
                    .languagePreference(initialLang)
                    .status(UserStatusEnum.APPROVED)
                    .mobileVerified(true)
                    .emailVerified(true)
                    .lastLoginAt(Instant.now())
                    .build();

            user = userRepository.save(user);

            auditLogRepository.save(AuditLogEntity.builder()
                    .actorUserId(user.getId())
                    .actionType("USER_REGISTERED_VIA_OTP")
                    .entityName("users")
                    .entityId(user.getId())
                    .newState("{\"mobile\":\"" + mobile + "\",\"role\":\"" + initialRole + "\"}")
                    .build());
        } else {
            user = existingUserOpt.get();
            user.setMobileVerified(true);
            user.setLastLoginAt(Instant.now());
            user = userRepository.save(user);

            auditLogRepository.save(AuditLogEntity.builder()
                    .actorUserId(user.getId())
                    .actionType("USER_LOGGED_IN_VIA_OTP")
                    .entityName("users")
                    .entityId(user.getId())
                    .build());
        }

        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = jwtTokenProvider.generateTokenFromPrincipal(principal, jwtExpirationMs);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        WorkerEntity worker = workerRepository.findByUserId(user.getId()).orElse(null);
        ProviderEntity provider = providerRepository.findByUserId(user.getId()).orElse(null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(UserProfileResponse.fromEntity(user, worker, provider, null))
                .isNewUser(isNewUser)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = jwtTokenProvider.generateTokenFromPrincipal(principal, jwtExpirationMs);

        WorkerEntity worker = workerRepository.findByUserId(user.getId()).orElse(null);
        ProviderEntity provider = providerRepository.findByUserId(user.getId()).orElse(null);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(UserProfileResponse.fromEntity(user, worker, provider, null))
                .isNewUser(false)
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        WorkerEntity worker = workerRepository.findByUserId(user.getId()).orElse(null);
        ProviderEntity provider = providerRepository.findByUserId(user.getId()).orElse(null);

        return UserProfileResponse.fromEntity(user, worker, provider, null);
    }

    /**
     * Check uniqueness of username, email, or mobile before registration
     */
    @Transactional(readOnly = true)
    public CheckUniquenessResponse checkUniqueness(String type, String value) {
        if (type == null || value == null || value.trim().isEmpty()) {
            return CheckUniquenessResponse.builder().available(true).field(type).message("Available").build();
        }

        String field = type.trim().toLowerCase();
        String val = value.trim();

        if ("username".equals(field)) {
            String norm = normalizeUsername(val);
            if (RESERVED_USERNAMES.contains(norm) || userRepository.existsByUsernameIgnoreCase(norm)) {
                return CheckUniquenessResponse.builder()
                        .available(false)
                        .field("username")
                        .message("Username already taken.")
                        .build();
            }
            return CheckUniquenessResponse.builder().available(true).field("username").message("Username is available.").build();
        } else if ("email".equals(field)) {
            String norm = normalizeEmail(val);
            if (userRepository.existsByEmailIgnoreCase(norm)) {
                return CheckUniquenessResponse.builder()
                        .available(false)
                        .field("email")
                        .message("Email already registered.")
                        .build();
            }
            return CheckUniquenessResponse.builder().available(true).field("email").message("Email is available.").build();
        } else if ("mobile".equals(field) || "phone".equals(field)) {
            if (isMobileRegistered(val)) {
                return CheckUniquenessResponse.builder()
                        .available(false)
                        .field("mobile")
                        .message("Mobile number already registered.")
                        .build();
            }
            return CheckUniquenessResponse.builder().available(true).field("mobile").message("Mobile number is available.").build();
        }

        return CheckUniquenessResponse.builder().available(true).field(field).message("Available").build();
    }

    /**
     * Activate or attach Worker profile to an existing User account (RULE 1 & 5)
     */
    @Transactional
    public UserProfileResponse activateWorkerProfile(UUID userId, WorkerProfileActivationRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        WorkerEntity worker = workerRepository.findByUserId(userId).orElse(null);
        if (worker == null) {
            String skillsStr = request.getSkills();
            if (skillsStr == null && request.getSkillsList() != null && !request.getSkillsList().isEmpty()) {
                skillsStr = String.join(",", request.getSkillsList());
            }
            if (skillsStr == null || skillsStr.isBlank()) {
                skillsStr = "cat.agriculture,cat.construction";
            }

            worker = WorkerEntity.builder()
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .countryId(user.getCountryId())
                    .stateId(user.getStateId())
                    .districtId(user.getDistrictId())
                    .talukaId(user.getTalukaId())
                    .villageId(user.getVillageId())
                    .village(user.getVillage() != null ? user.getVillage() : "रांजणगाव (Ranjangaon)")
                    .taluka(user.getTaluka() != null ? user.getTaluka() : "Shirur")
                    .district(user.getDistrict() != null ? user.getDistrict() : "Pune Rural")
                    .state(user.getState() != null ? user.getState() : "Maharashtra")
                    .country(user.getCountry() != null ? user.getCountry() : "India")
                    .skills(skillsStr)
                    .minDailyWage(request.getMinDailyWage() != null ? request.getMinDailyWage() : new BigDecimal("650.00"))
                    .travelRadiusKm(request.getTravelRadiusKm() != null ? request.getTravelRadiusKm() : 15)
                    .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 3)
                    .bio(request.getBio() != null ? request.getBio() : "स्थानिक कामासाठी अनुभवी व प्रामाणिक कामगार.")
                    .build();
            worker = workerRepository.save(worker);

            auditLogRepository.save(AuditLogEntity.builder()
                    .actorUserId(user.getId())
                    .actionType("WORKER_PROFILE_ACTIVATED")
                    .entityName("workers")
                    .entityId(worker.getId())
                    .build());
        }

        ProviderEntity provider = providerRepository.findByUserId(userId).orElse(null);
        return UserProfileResponse.fromEntity(user, worker, provider, "WORKER");
    }

    /**
     * Activate or attach Provider profile to an existing User account (RULE 1 & 5)
     */
    @Transactional
    public UserProfileResponse activateProviderProfile(UUID userId, ProviderProfileActivationRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        ProviderEntity provider = providerRepository.findByUserId(userId).orElse(null);
        if (provider == null) {
            String bName = request.getBusinessName();
            if (bName == null || bName.isBlank()) {
                bName = user.getFullName() + " फार्म्स";
            }

            provider = ProviderEntity.builder()
                    .userId(user.getId())
                    .name(user.getFullName())
                    .businessName(bName)
                    .providerType(request.getProviderType() != null ? request.getProviderType() : ProviderTypeEnum.FARMER)
                    .countryId(user.getCountryId())
                    .stateId(user.getStateId())
                    .districtId(user.getDistrictId())
                    .talukaId(user.getTalukaId())
                    .villageId(user.getVillageId())
                    .village(user.getVillage() != null ? user.getVillage() : "सासवड")
                    .taluka(user.getTaluka() != null ? user.getTaluka() : "Shirur")
                    .district(user.getDistrict() != null ? user.getDistrict() : "Pune Rural")
                    .state(user.getState() != null ? user.getState() : "Maharashtra")
                    .country(user.getCountry() != null ? user.getCountry() : "India")
                    .build();
            provider = providerRepository.save(provider);

            auditLogRepository.save(AuditLogEntity.builder()
                    .actorUserId(user.getId())
                    .actionType("PROVIDER_PROFILE_ACTIVATED")
                    .entityName("providers")
                    .entityId(provider.getId())
                    .build());
        }

        WorkerEntity worker = workerRepository.findByUserId(userId).orElse(null);
        return UserProfileResponse.fromEntity(user, worker, provider, "PROVIDER");
    }

    /**
     * Switch active role for multi-profile user session
     */
    @Transactional(readOnly = true)
    public UserProfileResponse switchActiveRole(UUID userId, String targetRole) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        WorkerEntity worker = workerRepository.findByUserId(userId).orElse(null);
        ProviderEntity provider = providerRepository.findByUserId(userId).orElse(null);

        String normalizedRole = targetRole != null ? targetRole.trim().toUpperCase() : "WORKER";
        if ("WORKER".equals(normalizedRole) && worker == null) {
            throw new IllegalArgumentException("Worker profile is not activated for this user.");
        }
        if ("PROVIDER".equals(normalizedRole) && provider == null) {
            throw new IllegalArgumentException("Provider profile is not activated for this user.");
        }
        if ("ADMIN".equals(normalizedRole) && user.getRole() != RoleEnum.ADMIN) {
            throw new IllegalArgumentException("Admin access denied.");
        }

        return UserProfileResponse.fromEntity(user, worker, provider, normalizedRole);
    }
}
