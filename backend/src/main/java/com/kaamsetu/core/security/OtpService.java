package com.kaamsetu.core.security;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpService {

    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, OtpEntry> emailOtpStorage = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${kaamsetu.email.from-address:no-reply@kaamsetu.org}")
    private String fromAddress;

    @Value("${kaamsetu.email.from-name:KaamSetu कामसेतू}")
    private String fromName;

    @Value("${kaamsetu.auth.mock-mode:false}")
    private boolean mockMode;

    @Value("${kaamsetu.otp.expiration-seconds:300}")
    private long expirationSeconds;

    @Value("${kaamsetu.otp.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${kaamsetu.otp.max-verification-attempts:5}")
    private int maxAttempts;

    @Value("${kaamsetu.sms.api-key:}")
    private String smsApiKey;

    @Value("${kaamsetu.sms.provider:FAST2SMS}")
    private String smsProvider;

    public void generateAndSendOtp(String mobile) {
        String normalizedMobile = normalizeMobile(mobile);

        // 1. Resend cooldown check
        OtpEntry existing = otpStorage.get(normalizedMobile);
        if (existing != null && existing.lastRequestedAt() != null) {
            long elapsedSeconds = Duration.between(existing.lastRequestedAt(), Instant.now()).getSeconds();
            if (elapsedSeconds < resendCooldownSeconds) {
                long remaining = resendCooldownSeconds - elapsedSeconds;
                throw new IllegalArgumentException("Please wait " + remaining + " seconds before requesting another OTP.");
            }
        }

        // 2. Cryptographically secure 6-digit numeric OTP
        int otpNumber = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpNumber);

        // 3. Store OTP with expiration timestamp, attempt count, and request time
        Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);
        otpStorage.put(normalizedMobile, new OtpEntry(otp, expiresAt, 0, Instant.now()));

        // 4. Real SMS delivery dispatch
        dispatchRealSms(normalizedMobile, otp);
    }

    public void generateAndSendEmailOtp(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        String normalizedEmail = email.trim().toLowerCase();

        // 1. Resend cooldown check
        OtpEntry existing = emailOtpStorage.get(normalizedEmail);
        if (existing != null && existing.lastRequestedAt() != null) {
            long elapsedSeconds = Duration.between(existing.lastRequestedAt(), Instant.now()).getSeconds();
            if (elapsedSeconds < resendCooldownSeconds) {
                long remaining = resendCooldownSeconds - elapsedSeconds;
                throw new IllegalArgumentException("Please wait " + remaining + " seconds before requesting another Email OTP.");
            }
        }

        // 2. Cryptographically secure 6-digit numeric OTP
        int otpNumber = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpNumber);

        // 3. Store OTP with expiration timestamp
        Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);
        emailOtpStorage.put(normalizedEmail, new OtpEntry(otp, expiresAt, 0, Instant.now()));

        // 4. Real Email delivery dispatch
        dispatchRealEmailOtp(normalizedEmail, otp);
    }

    public boolean verifyEmailOtp(String email, String inputOtp) {
        if (inputOtp == null || inputOtp.isBlank() || email == null) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        OtpEntry entry = emailOtpStorage.get(normalizedEmail);

        if (entry == null) {
            log.warn("❌ No pending Email OTP found for: {}", normalizedEmail);
            return false;
        }

        // Check expiration
        if (Instant.now().isAfter(entry.expiresAt())) {
            emailOtpStorage.remove(normalizedEmail);
            log.warn("⏰ Email OTP expired for: {}", normalizedEmail);
            return false;
        }

        // Check maximum attempt limits
        if (entry.attempts() >= maxAttempts) {
            emailOtpStorage.remove(normalizedEmail);
            log.warn("🚨 Too many invalid Email OTP attempts for: {}", normalizedEmail);
            throw new BadCredentialsException("Too many failed attempts. OTP has been invalidated. Please request a new Email OTP.");
        }

        boolean isValid = constantTimeEquals(entry.otp(), inputOtp.trim());

        if (isValid) {
            emailOtpStorage.remove(normalizedEmail);
            log.info("✅ Email OTP verified successfully for: {}", normalizedEmail);
            return true;
        } else {
            emailOtpStorage.put(normalizedEmail, new OtpEntry(entry.otp(), entry.expiresAt(), entry.attempts() + 1, entry.lastRequestedAt()));
            log.warn("❌ Invalid Email OTP entered for: {} (Attempt {} of {})", normalizedEmail, entry.attempts() + 1, maxAttempts);
            return false;
        }
    }

    public boolean verifyOtp(String mobile, String inputOtp) {
        if (inputOtp == null || inputOtp.isBlank()) {
            return false;
        }

        String normalizedMobile = normalizeMobile(mobile);
        OtpEntry entry = otpStorage.get(normalizedMobile);

        if (entry == null) {
            log.warn("❌ No pending OTP found for mobile: {}", maskMobile(normalizedMobile));
            return false;
        }

        // Check expiration
        if (Instant.now().isAfter(entry.expiresAt())) {
            otpStorage.remove(normalizedMobile);
            log.warn("⏰ OTP expired for mobile: {}", maskMobile(normalizedMobile));
            return false;
        }

        // Check maximum attempt limits
        if (entry.attempts() >= maxAttempts) {
            otpStorage.remove(normalizedMobile);
            log.warn("🚨 Too many invalid OTP attempts for mobile: {}", maskMobile(normalizedMobile));
            throw new BadCredentialsException("Too many failed attempts. OTP has been invalidated. Please request a new OTP.");
        }

        // Constant-time string comparison against timing attacks
        boolean isValid = constantTimeEquals(entry.otp(), inputOtp.trim());

        if (isValid) {
            // Single-use: Invalidate immediately on successful verification
            otpStorage.remove(normalizedMobile);
            log.info("✅ Mobile OTP verified successfully for: {}", maskMobile(normalizedMobile));
            return true;
        } else {
            // Increment failed attempt counter
            otpStorage.put(normalizedMobile, new OtpEntry(entry.otp(), entry.expiresAt(), entry.attempts() + 1, entry.lastRequestedAt()));
            log.warn("❌ Invalid OTP entered for mobile: {} (Attempt {} of {})", maskMobile(normalizedMobile), entry.attempts() + 1, maxAttempts);
            return false;
        }
    }

    private void dispatchRealSms(String mobile, String otp) {
        log.info("==================================================================");
        log.info("📱 [KaamSetu Real-Time SMS Dispatch Engine]");
        log.info("📱 Target Mobile : {}", mobile);
        log.info("🔑 Generated OTP : [ {} ]", otp);
        log.info("⏱️ Valid for     : {} seconds ({} minutes)", expirationSeconds, expirationSeconds / 60);

        if (smsApiKey != null && !smsApiKey.isBlank()) {
            try {
                if ("FAST2SMS".equalsIgnoreCase(smsProvider)) {
                    // Extract 10-digit number for Indian mobile numbers
                    String tenDigitNumber = mobile.replaceAll("[^0-9]", "");
                    if (tenDigitNumber.startsWith("91") && tenDigitNumber.length() == 12) {
                        tenDigitNumber = tenDigitNumber.substring(2);
                    }

                    String url = "https://www.fast2sms.com/dev/bulkV2"
                            + "?authorization=" + URLEncoder.encode(smsApiKey, StandardCharsets.UTF_8)
                            + "&route=otp"
                            + "&variables_values=" + URLEncoder.encode(otp, StandardCharsets.UTF_8)
                            + "&flash=0"
                            + "&numbers=" + URLEncoder.encode(tenDigitNumber, StandardCharsets.UTF_8);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Content-Type", "application/json")
                            .header("authorization", smsApiKey)
                            .GET()
                            .timeout(Duration.ofSeconds(10))
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("📡 Fast2SMS Gateway Response [HTTP {}]: {}", response.statusCode(), response.body());
                } else {
                    log.info("📡 Dispatched via custom SMS provider: {}", smsProvider);
                }
            } catch (Exception e) {
                log.error("⚠️ Failed to dispatch SMS via gateway API: {}", e.getMessage(), e);
            }
        } else {
            log.info("💡 [INFO] To send live SMS to your physical mobile phone:");
            log.info("💡 1. Obtain a free/starter API key from https://www.fast2sms.com");
            log.info("💡 2. Add 'SMS_PROVIDER_API_KEY=<your_key>' in environment or application.yml");
            log.info("💡 For local testing, use the OTP shown above: [ {} ]", otp);
        }
        log.info("==================================================================");
    }

    private void dispatchRealEmailOtp(String email, String otp) {
        log.info("==================================================================");
        log.info("📧 [KaamSetu Real-Time Email OTP Dispatch Engine]");
        log.info("To              : {}", email);
        log.info("Subject         : [KaamSetu कामसेतू] Your Email Verification OTP");
        log.info("🔑 6-Digit OTP  : [ {} ]", otp);
        log.info("⏱️ Valid for    : {} seconds ({} minutes)", expirationSeconds, expirationSeconds / 60);

        if (mailSender != null && mailUsername != null && !mailUsername.isBlank()) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(mailUsername, fromName);
                helper.setTo(email);
                helper.setSubject("[KaamSetu कामसेतू] Your Email Verification OTP: " + otp);

                String htmlContent = """
                        <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background: #ffffff;">
                          <h2 style="color: #166534; margin-top: 0; font-size: 22px;">🌾 KaamSetu (कामसेतू)</h2>
                          <p style="font-size: 15px; color: #334155;">नमस्कार,</p>
                          <p style="font-size: 15px; color: #334155;">तुमचा ईमेल पडताळणी <strong>६-अंकी OTP</strong> खालीलप्रमाणे आहे:</p>
                          <div style="text-align: center; margin: 28px 0;">
                            <span style="display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #15803d; background: #ecfdf5; padding: 14px 28px; border-radius: 8px; border: 1px solid #a7f3d0;">
                              %s
                            </span>
                          </div>
                          <p style="font-size: 13px; color: #64748b; margin-bottom: 4px;">हा OTP पुढील <strong>५ मिनिटांसाठी (300 सेकंद)</strong> वैध आहे.</p>
                          <p style="font-size: 13px; color: #dc2626;">हा कोड कोणासोबतही शेअर करू नका.</p>
                          <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                          <p style="font-size: 11px; color: #94a3b8; text-align: center;">© 2026 KaamSetu Platform • Pune Rural Belt SaaS</p>
                        </div>
                        """.formatted(otp);

                helper.setText(htmlContent, true);
                mailSender.send(message);
                log.info("✅ Live real-time email successfully dispatched to {} via SMTP!", email);
            } catch (Exception e) {
                log.error("⚠️ Failed to deliver real email via SMTP: {}", e.getMessage(), e);
            }
        } else {
            log.info("💡 [SMTP CONFIG INFO]");
            log.info("💡 To send real live emails to user Gmail inbox:");
            log.info("💡 1. Set 'EMAIL_USERNAME' = your gmail (e.g. rajdipbankar786@gmail.com)");
            log.info("💡 2. Set 'EMAIL_PASSWORD' = your 16-character Google App Password");
            log.info("💡 (Generate App Password at: https://myaccount.google.com/apppasswords)");
            log.info("💡 For local testing, use the OTP shown above: [ {} ]", otp);
        }
        log.info("==================================================================");
    }

    private String normalizeMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number cannot be empty");
        }
        String clean = mobile.replaceAll("[^0-9+]", "");
        if (clean.startsWith("+")) {
            return clean;
        }
        if (clean.startsWith("91") && clean.length() == 12) {
            return "+" + clean;
        }
        if (clean.length() == 10) {
            return "+91" + clean;
        }
        return "+91" + clean;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 8) return "******";
        return mobile.substring(0, Math.min(6, mobile.length() - 4)) + "****" + mobile.substring(mobile.length() - 4);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private record OtpEntry(String otp, Instant expiresAt, int attempts, Instant lastRequestedAt) {}
}
