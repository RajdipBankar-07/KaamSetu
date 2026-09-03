package com.kaamsetu.modules.auth.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.auth.dto.*;
import com.kaamsetu.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Mobile OTP + Email Verification + Username/Password + Admin Approval API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new Worker or Job Provider account (starts in PENDING status)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserProfileResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "auth.pendingApproval", "Your account is waiting for administrator approval."));
    }

    @PostMapping("/login")
    @Operation(summary = "Username / Mobile + Password authentication with Admin Approval check")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithPassword(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginWithPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.loginSuccess", "Authenticated successfully"));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address using cryptographically secure single-use token")
    public ResponseEntity<ApiResponse<UserProfileResponse>> verifyEmail(@RequestParam String token) {
        UserProfileResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.emailVerified", "Email address verified successfully"));
    }

    @PostMapping({"/email/resend", "/send-email-verification", "/email/send-verification"})
    @Operation(summary = "Send / Resend email verification link")
    public ResponseEntity<ApiResponse<Map<String, String>>> resendEmailVerification(@RequestParam String email) {
        authService.resendEmailVerification(email);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Verification email sent successfully")));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(null, "auth.passwordChanged", "Password changed successfully"));
    }

    @PostMapping({"/otp/send", "/send-otp"})
    @Operation(summary = "Send 6-digit OTP to Indian mobile number")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "auth.otpSent", "OTP sent successfully to " + request.getMobile()));
    }

    @PostMapping({"/otp/verify", "/verify-otp"})
    @Operation(summary = "Verify mobile OTP")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.mobileVerified", "Mobile verified successfully"));
    }

    @PostMapping({"/email/otp/send", "/send-email-otp"})
    @Operation(summary = "Send 6-digit real-time verification OTP to email address")
    public ResponseEntity<ApiResponse<Void>> sendEmailOtp(@Valid @RequestBody EmailOtpSendRequest request) {
        authService.sendEmailOtp(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "auth.emailOtpSent", "OTP sent successfully to " + request.getEmail()));
    }

    @PostMapping({"/email/otp/verify", "/verify-email-otp"})
    @Operation(summary = "Verify email 6-digit OTP in real-time")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest request) {
        authService.verifyEmailOtp(request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("verified", true), "auth.emailVerified", "Email verified successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token using valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.tokenRefreshed", "Access token refreshed"));
    }

    @GetMapping("/check-unique")
    @Operation(summary = "Check uniqueness of username, email, or mobile before account registration")
    public ResponseEntity<ApiResponse<CheckUniquenessResponse>> checkUnique(
            @RequestParam String type,
            @RequestParam String value) {
        CheckUniquenessResponse result = authService.checkUniqueness(type, value);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/profiles/worker")
    @Operation(summary = "Activate or create Worker Profile on existing User Account (RULE 1 & 5)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> activateWorkerProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) WorkerProfileActivationRequest request) {
        WorkerProfileActivationRequest req = request != null ? request : new WorkerProfileActivationRequest();
        UserProfileResponse response = authService.activateWorkerProfile(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.workerProfileActivated", "Worker profile activated successfully"));
    }

    @PostMapping("/profiles/provider")
    @Operation(summary = "Activate or create Job Provider Profile on existing User Account (RULE 1 & 5)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> activateProviderProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) ProviderProfileActivationRequest request) {
        ProviderProfileActivationRequest req = request != null ? request : new ProviderProfileActivationRequest();
        UserProfileResponse response = authService.activateProviderProfile(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.providerProfileActivated", "Job Provider profile activated successfully"));
    }

    @PostMapping("/profiles/switch-role")
    @Operation(summary = "Switch active role mode for dual-profile account")
    public ResponseEntity<ApiResponse<UserProfileResponse>> switchActiveRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody SwitchRoleRequest request) {
        UserProfileResponse response = authService.switchActiveRole(principal.getId(), request.getRole());
        return ResponseEntity.ok(ApiResponse.ok(response, "auth.roleSwitched", "Active profile mode switched to " + request.getRole()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user session context and profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse userProfile = authService.getCurrentUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(userProfile));
    }
}
