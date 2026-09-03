package com.kaamsetu.core.config;

import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ensures database columns accommodate all status enums and initializes Super Administrator.
 * Admin Credentials:
 *   Username: Admin
 *   Email: rajdipbankar786@gmail.com
 *   Password: Admin@07
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // 1. Auto-patch MySQL database schema to prevent "Data truncated for column 'status'"
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN status VARCHAR(30) NOT NULL DEFAULT 'PENDING'");
        } catch (Exception e) {
            log.debug("Notice on users.status schema check: {}", e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role VARCHAR(30) NOT NULL DEFAULT 'WORKER'");
        } catch (Exception e) {
            log.debug("Notice on users.role schema check: {}", e.getMessage());
        }

        String adminUsername = "Admin";
        String adminEmail = "rajdipbankar786@gmail.com";
        String adminRawPassword = "Admin@07";
        String adminMobile = "+919900000001";

        Optional<UserEntity> existingAdmin = userRepository.findByUsernameIgnoreCase(adminUsername)
                .or(() -> userRepository.findByEmailIgnoreCase(adminEmail));

        if (existingAdmin.isPresent()) {
            UserEntity admin = existingAdmin.get();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminRawPassword));
            admin.setRole(RoleEnum.ADMIN);
            admin.setStatus(UserStatusEnum.APPROVED);
            admin.setMobileVerified(true);
            admin.setEmailVerified(true);
            admin.setLocationVerified(true);
            admin.setIdentityVerified(true);
            userRepository.save(admin);
            log.info("🛡️ [AdminDataInitializer] Super Admin credentials refreshed: username='{}', email='{}'", adminUsername, adminEmail);
        } else {
            UserEntity admin = UserEntity.builder()
                    .username(adminUsername)
                    .fullName("Rajdip Bankar (Super Admin)")
                    .email(adminEmail)
                    .mobile(adminMobile)
                    .passwordHash(passwordEncoder.encode(adminRawPassword))
                    .role(RoleEnum.ADMIN)
                    .languagePreference(LanguageCodeEnum.mr)
                    .status(UserStatusEnum.APPROVED)
                    .country("India")
                    .state("Maharashtra")
                    .district("Pune Rural")
                    .village("शिरूर (Shirur)")
                    .mobileVerified(true)
                    .emailVerified(true)
                    .locationVerified(true)
                    .identityVerified(true)
                    .build();
            userRepository.save(admin);
            log.info("🛡️ [AdminDataInitializer] Super Admin initialized successfully: username='{}', email='{}'", adminUsername, adminEmail);
        }
    }
}
