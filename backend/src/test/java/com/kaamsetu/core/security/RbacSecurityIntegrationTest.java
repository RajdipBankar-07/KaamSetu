package com.kaamsetu.core.security;

import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RbacSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String workerToken;
    private String providerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        workerRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Create Worker User & Profile
        UserEntity workerUser = userRepository.save(UserEntity.builder()
                .mobile("+919822011111")
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.ACTIVE)
                .mobileVerified(true)
                .build());

        workerRepository.save(WorkerEntity.builder()
                .userId(workerUser.getId())
                .fullName("Rahul Worker")
                .village("Shirur")
                .taluka("Shirur")
                .district("Pune Rural")
                .build());

        workerToken = jwtTokenProvider.generateTokenFromPrincipal(UserPrincipal.create(workerUser), 3600000);

        // 2. Create Provider User & Profile
        UserEntity providerUser = userRepository.save(UserEntity.builder()
                .mobile("+919822022222")
                .role(RoleEnum.PROVIDER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.ACTIVE)
                .mobileVerified(true)
                .build());

        providerRepository.save(ProviderEntity.builder()
                .userId(providerUser.getId())
                .name("Balasaheb Provider")
                .village("Saswad")
                .taluka("Purandar")
                .district("Pune Rural")
                .build());

        providerToken = jwtTokenProvider.generateTokenFromPrincipal(UserPrincipal.create(providerUser), 3600000);

        // 3. Create Admin User
        UserEntity adminUser = userRepository.save(UserEntity.builder()
                .mobile("+919822033333")
                .role(RoleEnum.ADMIN)
                .languagePreference(LanguageCodeEnum.en)
                .status(UserStatusEnum.ACTIVE)
                .mobileVerified(true)
                .build());

        adminToken = jwtTokenProvider.generateTokenFromPrincipal(UserPrincipal.create(adminUser), 3600000);
    }

    @Test
    @DisplayName("RBAC - Worker can access /worker/** endpoints")
    void workerCanAccessWorkerEndpoints() throws Exception {
        mockMvc.perform(get("/worker/profile")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Rahul Worker"));
    }

    @Test
    @DisplayName("RBAC - Worker CANNOT access /provider/** endpoints (403 FORBIDDEN)")
    void workerCannotAccessProviderEndpoints() throws Exception {
        mockMvc.perform(get("/provider/profile")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("RBAC - Worker CANNOT access /admin/** endpoints (403 FORBIDDEN)")
    void workerCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/health/kpis")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("RBAC - Provider can access /provider/** endpoints")
    void providerCanAccessProviderEndpoints() throws Exception {
        mockMvc.perform(get("/provider/profile")
                        .header("Authorization", "Bearer " + providerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Balasaheb Provider"));
    }

    @Test
    @DisplayName("RBAC - Provider CANNOT access /admin/** endpoints (403 FORBIDDEN)")
    void providerCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/health/kpis")
                        .header("Authorization", "Bearer " + providerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("RBAC - Admin can access /admin/** endpoints")
    void adminCanAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/health/kpis")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.systemStatus").value("OPERATIONAL_HEALTHY"));
    }

    @Test
    @DisplayName("RBAC - Unauthenticated request is rejected with 401 UNAUTHORIZED")
    void unauthenticatedRequestRejected() throws Exception {
        mockMvc.perform(get("/worker/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }
}
