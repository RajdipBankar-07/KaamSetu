package com.kaamsetu.core.config;

import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Initializes Development & Testing Accounts using the standardized credential rule:
 *   USERNAME = user's name
 *   PASSWORD = user's name + "@123"
 *
 * Example Test Accounts:
 *   - Suresh (Worker):    Username: Suresh   | Password: Suresh@123
 *   - Ganesh (Worker):    Username: Ganesh   | Password: Ganesh@123
 *   - Raju   (Worker):    Username: Raju     | Password: Raju@123
 *   - Pooja  (Worker):    Username: Pooja    | Password: Pooja@123
 *   - Mahesh (Provider):  Username: Mahesh   | Password: Mahesh@123
 *   - Ramesh (Provider):  Username: Ramesh   | Password: Ramesh@123
 *   - Dinesh (Provider):  Username: Dinesh   | Password: Dinesh@123
 *
 * NOTE: Passwords are NEVER stored in plaintext. They are encoded with BCrypt.
 *       This credential rule is strictly for development and automated testing environments.
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class DevTestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.kaamsetu.modules.worker.repository.WorkerRepository workerRepository;
    private final com.kaamsetu.modules.provider.repository.ProviderRepository providerRepository;

    @Override
    public void run(String... args) {
        log.info("🌾 [DevTestDataInitializer] Initializing standardized test accounts (Username = Name, Password = Name@123)...");

        // Standardized Test Workers
        seedOrUpdateTestUser(
                "Suresh",
                "सुरेश जाधव (Suresh Jadhav)",
                "suresh.dev@kaamsetu.in",
                "+919822000001",
                RoleEnum.WORKER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-pune", "Pune Rural",
                "tal-shirur",
                "vil-ranjangaon", "रांजणगाव (Ranjangaon)"
        );

        seedOrUpdateTestUser(
                "Ganesh",
                "गणेश शिंदे (Ganesh Shinde)",
                "ganesh.dev@kaamsetu.in",
                "+919822000002",
                RoleEnum.WORKER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-solapur", "Solapur",
                "subdist-pandharpur",
                "vil-wakhari", "वाखरी (Wakhari)"
        );

        seedOrUpdateTestUser(
                "Raju",
                "राजू मोरे (Raju More)",
                "raju.dev@kaamsetu.in",
                "+919822000003",
                RoleEnum.WORKER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-pune", "Pune Rural",
                "tal-haveli",
                "vil-wagholi", "वाघोली (Wagholi)"
        );

        seedOrUpdateTestUser(
                "Pooja",
                "पूजा पवार (Pooja Pawar)",
                "pooja.dev@kaamsetu.in",
                "+919822000004",
                RoleEnum.WORKER,
                GenderEnum.FEMALE,
                "state-mh", "Maharashtra",
                "dist-solapur", "Solapur",
                "subdist-barshi",
                "vil-bairagwadi", "वैराग (Vairag)"
        );

        // Standardized Test Job Providers
        seedOrUpdateTestUser(
                "Mahesh",
                "महेश पाटील (Mahesh Patil)",
                "mahesh.dev@kaamsetu.in",
                "+919822000011",
                RoleEnum.PROVIDER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-pune", "Pune Rural",
                "tal-shirur",
                "vil-shirur-rural", "शिरूर ग्रामीण"
        );

        seedOrUpdateTestUser(
                "Ramesh",
                "रमेश कुलकर्णी (Ramesh Kulkarni)",
                "ramesh.dev@kaamsetu.in",
                "+919822000012",
                RoleEnum.PROVIDER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-solapur", "Solapur",
                "subdist-pandharpur",
                "vil-pandharpur-rural", "पंढरपूर ग्रामीण"
        );

        seedOrUpdateTestUser(
                "Sunita",
                "सुनिता शिंदे (Sunita Shinde)",
                "sunita.dev@kaamsetu.in",
                "+919822000014",
                RoleEnum.PROVIDER,
                GenderEnum.FEMALE,
                "state-mh", "Maharashtra",
                "dist-pune", "Pune Rural",
                "tal-shirur",
                "vil-ranjangaon", "रांजणगाव (Ranjangaon)"
        );

        seedOrUpdateTestUser(
                "Dinesh",
                "दिनेश सावंत (Dinesh Sawant)",
                "dinesh.dev@kaamsetu.in",
                "+919822000013",
                RoleEnum.PROVIDER,
                GenderEnum.MALE,
                "state-mh", "Maharashtra",
                "dist-satara", "Satara",
                "subdist-karad",
                "vil-karad-rural", "कराड ग्रामीण"
        );

        log.info("✅ [DevTestDataInitializer] All development/test accounts configured with BCrypt hashes.");
    }

    private void seedOrUpdateTestUser(
            String username,
            String fullName,
            String email,
            String mobile,
            RoleEnum role,
            GenderEnum gender,
            String stateId, String state,
            String districtId, String district,
            String talukaId,
            String villageId, String village
    ) {
        String rawPassword = username + "@123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Optional<UserEntity> existing = userRepository.findByUsernameIgnoreCase(username)
                .or(() -> userRepository.findByEmailIgnoreCase(email))
                .or(() -> userRepository.findByMobile(mobile));

        if (existing.isPresent()) {
            UserEntity user = existing.get();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setPasswordHash(encodedPassword);
            user.setRole(role);
            user.setGender(gender != null ? gender : GenderEnum.MALE);
            user.setStatus(UserStatusEnum.APPROVED);
            user.setCountryId("IN");
            user.setCountry("India");
            user.setStateId(stateId);
            user.setState(state);
            user.setDistrictId(districtId);
            user.setDistrict(district);
            user.setTalukaId(talukaId);
            user.setVillageId(villageId);
            user.setVillage(village);
            user.setMobileVerified(true);
            user.setEmailVerified(true);
            user.setLocationVerified(true);
            user.setIdentityVerified(true);
            UserEntity saved = userRepository.save(user);
            syncRoleEntity(saved);
            log.info("👤 [DevTestDataInitializer] Refreshed test user '{}' (Password: {}@123, Role: {})", username, username, role);
        } else {
            UserEntity user = UserEntity.builder()
                    .username(username)
                    .fullName(fullName)
                    .email(email)
                    .mobile(mobile)
                    .passwordHash(encodedPassword)
                    .role(role)
                    .gender(gender != null ? gender : GenderEnum.MALE)
                    .languagePreference(LanguageCodeEnum.mr)
                    .status(UserStatusEnum.APPROVED)
                    .countryId("IN")
                    .country("India")
                    .stateId(stateId)
                    .state(state)
                    .districtId(districtId)
                    .district(district)
                    .talukaId(talukaId)
                    .villageId(villageId)
                    .village(village)
                    .mobileVerified(true)
                    .emailVerified(true)
                    .locationVerified(true)
                    .identityVerified(true)
                    .build();
            UserEntity saved = userRepository.save(user);
            syncRoleEntity(saved);
            log.info("👤 [DevTestDataInitializer] Created test user '{}' (Password: {}@123, Role: {})", username, username, role);
        }
    }

    private void syncRoleEntity(UserEntity user) {
        if (user.getRole() == RoleEnum.WORKER) {
            workerRepository.findByUserId(user.getId()).orElseGet(() -> {
                com.kaamsetu.modules.worker.entity.WorkerEntity w = com.kaamsetu.modules.worker.entity.WorkerEntity.builder()
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .countryId(user.getCountryId())
                        .stateId(user.getStateId())
                        .districtId(user.getDistrictId())
                        .talukaId(user.getTalukaId())
                        .villageId(user.getVillageId())
                        .village(user.getVillage())
                        .taluka(user.getVillage())
                        .district(user.getDistrict())
                        .state(user.getState())
                        .country(user.getCountry())
                        .minDailyWage(new java.math.BigDecimal("650.00"))
                        .ratingAvg(new java.math.BigDecimal("4.8"))
                        .trustIndex(new java.math.BigDecimal("4.9"))
                        .availableToday(true)
                        .skills("[\"cat.agriculture\", \"cat.construction\"]")
                        .availabilityDays("{\"Mon\":true,\"Tue\":true,\"Wed\":true,\"Thu\":true,\"Fri\":true,\"Sat\":true,\"Sun\":false}")
                        .travelRadiusKm(15)
                        .build();
                return workerRepository.save(w);
            });
        } else if (user.getRole() == RoleEnum.PROVIDER) {
            providerRepository.findByUserId(user.getId()).orElseGet(() -> {
                com.kaamsetu.modules.provider.entity.ProviderEntity p = com.kaamsetu.modules.provider.entity.ProviderEntity.builder()
                        .userId(user.getId())
                        .name(user.getFullName())
                        .businessName(user.getFullName())
                        .countryId(user.getCountryId())
                        .stateId(user.getStateId())
                        .districtId(user.getDistrictId())
                        .talukaId(user.getTalukaId())
                        .villageId(user.getVillageId())
                        .village(user.getVillage())
                        .taluka(user.getVillage())
                        .district(user.getDistrict())
                        .state(user.getState())
                        .country(user.getCountry())
                        .providerType(com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum.FARMER)
                        .ratingAvg(new java.math.BigDecimal("4.9"))
                        .trustIndex(new java.math.BigDecimal("5.0"))
                        .build();
                return providerRepository.save(p);
            });
        }
    }
}
