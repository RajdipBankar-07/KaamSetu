package com.kaamsetu.core.security;

import com.kaamsetu.modules.admin.entity.AuditLogEntity;
import com.kaamsetu.modules.admin.repository.AuditLogRepository;
import com.kaamsetu.modules.auth.entity.EmailVerificationTokenEntity;
import com.kaamsetu.modules.auth.repository.EmailVerificationTokenRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${kaamsetu.email.from-address:no-reply@kaamsetu.org}")
    private String fromAddress;

    @Value("${kaamsetu.email.from-name:KaamSetu कामसेतू}")
    private String fromName;

    @Value("${kaamsetu.email.verification-expiration-hours:24}")
    private long expirationHours;

    @Value("${kaamsetu.app.base-url:http://localhost:8088}")
    private String appBaseUrl;

    /**
     * Generate cryptographically secure single-use email verification token
     */
    @Transactional
    public String createAndSendVerificationEmail(UUID userId, String email, String fullName) {
        // Invalidate previous unused tokens for this email
        tokenRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email).ifPresent(prev -> {
            prev.setUsed(true);
            tokenRepository.save(prev);
        });

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        Instant expiresAt = Instant.now().plus(expirationHours, ChronoUnit.HOURS);

        EmailVerificationTokenEntity tokenEntity = EmailVerificationTokenEntity.builder()
                .userId(userId)
                .email(email.toLowerCase().trim())
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        tokenRepository.save(tokenEntity);

        String verificationLink = appBaseUrl + "/api/v1/auth/verify-email?token=" + token;

        // Structured email delivery dispatch
        dispatchRealEmail(email, fullName, verificationLink, expirationHours);

        auditLogRepository.save(AuditLogEntity.builder()
                .actorUserId(userId)
                .actionType("EMAIL_VERIFICATION_DISPATCHED")
                .entityName("email_verification_tokens")
                .newState("{\"email\":\"" + email + "\",\"expiresAt\":\"" + expiresAt + "\"}")
                .build());

        return token;
    }

    /**
     * Validate verification token and mark user's email as verified
     */
    @Transactional
    public UserEntity verifyEmailToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BadCredentialsException("Verification token cannot be empty");
        }

        EmailVerificationTokenEntity tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid email verification token"));

        if (tokenEntity.isUsed()) {
            throw new BadCredentialsException("Verification link has already been used");
        }

        if (tokenEntity.isExpired()) {
            throw new BadCredentialsException("Email verification link has expired. Please request a new link.");
        }

        // Mark token as consumed
        tokenEntity.setUsed(true);
        tokenRepository.save(tokenEntity);

        // Find user and update emailVerified flag
        UserEntity user = userRepository.findById(tokenEntity.getUserId())
                .or(() -> userRepository.findByEmailIgnoreCase(tokenEntity.getEmail()))
                .orElseThrow(() -> new BadCredentialsException("User associated with verification token not found"));

        user.setEmailVerified(true);
        UserEntity savedUser = userRepository.save(user);

        auditLogRepository.save(AuditLogEntity.builder()
                .actorUserId(savedUser.getId())
                .actionType("USER_EMAIL_VERIFIED")
                .entityName("users")
                .entityId(savedUser.getId())
                .newState("{\"email\":\"" + savedUser.getEmail() + "\",\"verified\":true}")
                .build());

        log.info("✅ Email successfully verified for user: {} ({})", savedUser.getUsername(), savedUser.getEmail());
        return savedUser;
    }

    private void dispatchRealEmail(String email, String fullName, String verificationLink, long expirationHours) {
        log.info("==================================================================");
        log.info("📧 [KaamSetu Email Dispatch Engine]");
        log.info("To: {} <{}>", fullName != null ? fullName : "User", email);
        log.info("Subject: [KaamSetu कामसेतू] Verify your email address");
        log.info("Verification Link: {}", verificationLink);
        log.info("Expires in: {} hours", expirationHours);

        if (mailSender != null && mailUsername != null && !mailUsername.isBlank()) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(mailUsername, fromName);
                helper.setTo(email);
                helper.setSubject("[KaamSetu कामसेतू] Verify your email address");

                String htmlContent = """
                        <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background: #ffffff;">
                          <h2 style="color: #166534; margin-top: 0; font-size: 22px;">🌾 KaamSetu (कामसेतू)</h2>
                          <p style="font-size: 15px; color: #334155;">नमस्कार %s,</p>
                          <p style="font-size: 15px; color: #334155;">कामसेतू प्लॅटफॉर्मवर तुमचे स्वागत आहे. कृपया तुमचा ईमेल पडताळण्यासाठी खालील बटणावर क्लिक करा:</p>
                          <div style="text-align: center; margin: 28px 0;">
                            <a href="%s" style="display: inline-block; font-size: 16px; font-weight: bold; color: #ffffff; background: #16a34a; padding: 12px 28px; border-radius: 8px; text-decoration: none;">
                              ✓ ईमेल पडताळणी करा (Verify Email)
                            </a>
                          </div>
                          <p style="font-size: 13px; color: #64748b;">ही लिंक पुढील <strong>%d तासांसाठी</strong> वैध आहे.</p>
                          <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                          <p style="font-size: 11px; color: #94a3b8; text-align: center;">© 2026 KaamSetu Platform • Pune Rural Belt SaaS</p>
                        </div>
                        """.formatted(fullName != null ? fullName : "User", verificationLink, expirationHours);

                helper.setText(htmlContent, true);
                mailSender.send(message);
                log.info("✅ Verification email successfully dispatched to {} via SMTP server!", email);
            } catch (Exception e) {
                log.error("⚠️ Failed to deliver verification email via SMTP: {}", e.getMessage(), e);
            }
        }
        log.info("==================================================================");
    }
}
