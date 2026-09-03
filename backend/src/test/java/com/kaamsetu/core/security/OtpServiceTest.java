package com.kaamsetu.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
        ReflectionTestUtils.setField(otpService, "expirationSeconds", 300L);
        ReflectionTestUtils.setField(otpService, "resendCooldownSeconds", 60L);
        ReflectionTestUtils.setField(otpService, "maxAttempts", 5);
        ReflectionTestUtils.setField(otpService, "mockMode", false);
    }

    @Test
    @DisplayName("Should generate 6-digit numeric OTP and verify successfully")
    void shouldGenerateAndVerifyOtp() {
        String mobile = "+919822012345";
        otpService.generateAndSendOtp(mobile);

        // Verify that random invalid OTP is rejected
        assertFalse(otpService.verifyOtp(mobile, "000000"));
    }

    @Test
    @DisplayName("Should reject invalid OTP and enforce attempt limits")
    void shouldEnforceAttemptLimits() {
        String mobile = "+919822019999";
        otpService.generateAndSendOtp(mobile);

        assertFalse(otpService.verifyOtp(mobile, "111111"));
        assertFalse(otpService.verifyOtp(mobile, "222222"));
        assertFalse(otpService.verifyOtp(mobile, "333333"));
        assertFalse(otpService.verifyOtp(mobile, "444444"));
        assertFalse(otpService.verifyOtp(mobile, "555555"));

        // 6th attempt throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> otpService.verifyOtp(mobile, "666666"));
    }

    @Test
    @DisplayName("Should enforce resend cooldown")
    void shouldEnforceResendCooldown() {
        String mobile = "+919850066778";
        otpService.generateAndSendOtp(mobile);

        // Immediate resend should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> otpService.generateAndSendOtp(mobile));
    }

    @Test
    @DisplayName("Should reject blank or null OTP")
    void shouldRejectBlankOrNullOtp() {
        String mobile = "+919822012345";
        assertFalse(otpService.verifyOtp(mobile, ""));
        assertFalse(otpService.verifyOtp(mobile, null));
    }

    @Test
    @DisplayName("Should generate 6-digit numeric Email OTP and enforce cooldown")
    void shouldGenerateEmailOtpAndEnforceCooldown() {
        String email = "rajdipbankar786@gmail.com";
        otpService.generateAndSendEmailOtp(email);

        assertFalse(otpService.verifyEmailOtp(email, "000000"));
        assertThrows(IllegalArgumentException.class, () -> otpService.generateAndSendEmailOtp(email));
    }

    @Test
    @DisplayName("Should reject invalid Email OTP and blank input")
    void shouldRejectInvalidEmailOtp() {
        String email = "test@kaamsetu.org";
        otpService.generateAndSendEmailOtp(email);

        assertFalse(otpService.verifyEmailOtp(email, ""));
        assertFalse(otpService.verifyEmailOtp(email, null));
        assertFalse(otpService.verifyEmailOtp(email, "999999"));
    }
}
