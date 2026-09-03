package com.kaamsetu.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaamsetu.core.security.JwtTokenProvider;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.auth.dto.*;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.entity.enums.ProviderTypeEnum;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KaamSetuGlobalIdentityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        workerRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String getAuthBearerToken(UserEntity user) {
        boolean hasWorker = workerRepository.findByUserId(user.getId()).isPresent();
        boolean hasProvider = providerRepository.findByUserId(user.getId()).isPresent();
        UserPrincipal principal = UserPrincipal.create(user, hasWorker, hasProvider);
        return "Bearer " + jwtTokenProvider.generateTokenFromPrincipal(principal, 600000);
    }

    @Test
    @DisplayName("ACCOUNT-001: Create new Worker -> 1 User Account + 1 Worker Profile")
    void testAccount001_CreateNewWorker() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("9000000001")
                .password("Suresh@123")
                .confirmPassword("Suresh@123")
                .role(RoleEnum.WORKER)
                .village("Ranjangaon")
                .countryId("IN")
                .stateId("state-mh")
                .districtId("dist-pune")
                .talukaId("tal-shirur")
                .villageId("vil-ranjangaon")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("suresh"))
                .andExpect(jsonPath("$.data.hasWorkerProfile").value(true))
                .andExpect(jsonPath("$.data.hasProviderProfile").value(false));

        assertEquals(1, userRepository.count());
        assertEquals(1, workerRepository.count());
        assertEquals(0, providerRepository.count());
    }

    @Test
    @DisplayName("ACCOUNT-002: Create new Provider -> 1 User Account + 1 Provider Profile")
    void testAccount002_CreateNewProvider() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("ramesh")
                .fullName("Ramesh Shinde")
                .email("ramesh@example.com")
                .mobile("9000000002")
                .password("Ramesh@123")
                .confirmPassword("Ramesh@123")
                .role(RoleEnum.PROVIDER)
                .village("Saswad")
                .countryId("IN")
                .stateId("state-mh")
                .districtId("dist-pune")
                .talukaId("tal-shirur")
                .villageId("vil-ranjangaon")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("ramesh"))
                .andExpect(jsonPath("$.data.hasWorkerProfile").value(false))
                .andExpect(jsonPath("$.data.hasProviderProfile").value(true));

        assertEquals(1, userRepository.count());
        assertEquals(0, workerRepository.count());
        assertEquals(1, providerRepository.count());
    }

    @Test
    @DisplayName("ACCOUNT-003 & ACCOUNT-010 & ACCOUNT-011: Existing Worker creates Provider Profile -> 1 User Account + Both Profiles")
    void testAccount003_ExistingWorkerCreatesProviderProfile() throws Exception {
        UserEntity user = userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        workerRepository.save(WorkerEntity.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .village(user.getVillage())
                .taluka("Shirur")
                .build());

        String token = getAuthBearerToken(user);

        ProviderProfileActivationRequest provReq = ProviderProfileActivationRequest.builder()
                .businessName("Suresh Farm Operations")
                .providerType(ProviderTypeEnum.FARMER)
                .build();

        mockMvc.perform(post("/auth/profiles/provider")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasWorkerProfile").value(true))
                .andExpect(jsonPath("$.data.hasProviderProfile").value(true));

        // CRITICAL CHECK: Still exactly ONE user account, but both profile records exist!
        assertEquals(1, userRepository.count(), "Must NOT create a second users row");
        assertEquals(1, workerRepository.count(), "Must have 1 worker profile");
        assertEquals(1, providerRepository.count(), "Must have 1 provider profile");
    }

    @Test
    @DisplayName("ACCOUNT-004: Existing Provider creates Worker Profile -> 1 User Account + Both Profiles")
    void testAccount004_ExistingProviderCreatesWorkerProfile() throws Exception {
        UserEntity user = userRepository.save(UserEntity.builder()
                .username("ramesh")
                .fullName("Ramesh Shinde")
                .email("ramesh@example.com")
                .mobile("+919000000002")
                .passwordHash(passwordEncoder.encode("Ramesh@123"))
                .role(RoleEnum.PROVIDER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Saswad")
                .build());

        providerRepository.save(ProviderEntity.builder()
                .userId(user.getId())
                .name(user.getFullName())
                .village(user.getVillage())
                .taluka("Shirur")
                .build());

        String token = getAuthBearerToken(user);

        WorkerProfileActivationRequest workerReq = WorkerProfileActivationRequest.builder()
                .skills("cat.agriculture,cat.carpentry")
                .minDailyWage(new BigDecimal("700.00"))
                .travelRadiusKm(20)
                .experienceYears(4)
                .build();

        mockMvc.perform(post("/auth/profiles/worker")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasWorkerProfile").value(true))
                .andExpect(jsonPath("$.data.hasProviderProfile").value(true));

        assertEquals(1, userRepository.count());
        assertEquals(1, workerRepository.count());
        assertEquals(1, providerRepository.count());
    }

    @Test
    @DisplayName("ACCOUNT-005 & ACCOUNT-009: Second account uses existing email -> REJECT with 'Email already registered.'")
    void testAccount005_RejectDuplicateEmail() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        RegisterRequest req = RegisterRequest.builder()
                .username("ramesh")
                .fullName("Ramesh Shinde")
                .email("suresh@example.com") // DUPLICATE EMAIL
                .mobile("9000000002")
                .password("Ramesh@123")
                .confirmPassword("Ramesh@123")
                .role(RoleEnum.PROVIDER)
                .village("Saswad")
                .countryId("IN")
                .stateId("state-mh")
                .districtId("dist-pune")
                .talukaId("tal-shirur")
                .villageId("vil-ranjangaon")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered."));
    }

    @Test
    @DisplayName("ACCOUNT-006 & ACCOUNT-008: Second account uses existing mobile -> REJECT with 'Mobile number already registered.'")
    void testAccount006_RejectDuplicateMobile() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        RegisterRequest req = RegisterRequest.builder()
                .username("ramesh")
                .fullName("Ramesh Shinde")
                .email("ramesh@example.com")
                .mobile("9000000001") // DUPLICATE MOBILE
                .password("Ramesh@123")
                .confirmPassword("Ramesh@123")
                .role(RoleEnum.PROVIDER)
                .village("Saswad")
                .countryId("IN")
                .stateId("state-mh")
                .districtId("dist-pune")
                .talukaId("tal-shirur")
                .villageId("vil-ranjangaon")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mobile number already registered."));
    }

    @Test
    @DisplayName("ACCOUNT-007: Second account uses existing username -> REJECT with 'Username already taken.'")
    void testAccount007_RejectDuplicateUsername() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        RegisterRequest req = RegisterRequest.builder()
                .username("Suresh") // DUPLICATE USERNAME
                .fullName("Mahesh Sawant")
                .email("mahesh@example.com")
                .mobile("9000000003")
                .password("Mahesh@123")
                .confirmPassword("Mahesh@123")
                .role(RoleEnum.PROVIDER)
                .village("Saswad")
                .countryId("IN")
                .stateId("state-mh")
                .districtId("dist-pune")
                .talukaId("tal-shirur")
                .villageId("vil-ranjangaon")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username already taken."));
    }

    @Test
    @DisplayName("ACCOUNT-014: Username case & whitespace variation normalization")
    void testAccount014_UsernameCaseVariation() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        // Test GET /auth/check-unique with uppercase and leading/trailing whitespace
        mockMvc.perform(get("/auth/check-unique?type=username&value=  SuReSh  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.message").value("Username already taken."));
    }

    @Test
    @DisplayName("ACCOUNT-015: Email case variation normalization")
    void testAccount015_EmailCaseVariation() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        // Test GET /auth/check-unique with uppercase email
        mockMvc.perform(get("/auth/check-unique?type=email&value=SURESH@EXAMPLE.COM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.message").value("Email already registered."));
    }

    @Test
    @DisplayName("ACCOUNT-016: Mobile formatting variation normalization")
    void testAccount016_MobileFormattingVariation() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("suresh")
                .fullName("Suresh Patil")
                .email("suresh@example.com")
                .mobile("+919000000001")
                .passwordHash(passwordEncoder.encode("Suresh@123"))
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.APPROVED)
                .village("Ranjangaon")
                .build());

        // Test check with 10 digits without +91
        mockMvc.perform(get("/auth/check-unique?type=mobile&value=9000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.message").value("Mobile number already registered."));
    }

    @Test
    @DisplayName("ACCOUNT-012: Race condition - Two simultaneous registrations with same email -> ONLY ONE account created")
    void testAccount012_SimultaneousRegistrationsSameEmail() throws Exception {
        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    RegisterRequest req = RegisterRequest.builder()
                            .username("user_race_email_" + idx)
                            .fullName("User " + idx)
                            .email("race_email@example.com") // SAME EMAIL
                            .mobile("900000001" + idx)
                            .password("Pass@123")
                            .confirmPassword("Pass@123")
                            .role(RoleEnum.WORKER)
                            .village("Ranjangaon")
                            .countryId("IN")
                            .stateId("state-mh")
                            .districtId("dist-pune")
                            .talukaId("tal-shirur")
                            .villageId("vil-ranjangaon")
                            .build();

                    var res = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))).andReturn();

                    if (res.getResponse().getStatus() == 201) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {}
            });
        }

        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(1, successCount.get(), "Exactly ONE user registration should succeed with duplicate email in race condition");
    }

    @Test
    @DisplayName("ACCOUNT-013: Race condition - Two simultaneous registrations with same mobile -> ONLY ONE account created")
    void testAccount013_SimultaneousRegistrationsSameMobile() throws Exception {
        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    RegisterRequest req = RegisterRequest.builder()
                            .username("user_race_mob_" + idx)
                            .fullName("User " + idx)
                            .email("race_mob_" + idx + "@example.com")
                            .mobile("9888888888") // SAME MOBILE
                            .password("Pass@123")
                            .confirmPassword("Pass@123")
                            .role(RoleEnum.PROVIDER)
                            .village("Saswad")
                            .countryId("IN")
                            .stateId("state-mh")
                            .districtId("dist-pune")
                            .talukaId("tal-shirur")
                            .villageId("vil-ranjangaon")
                            .build();

                    var res = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))).andReturn();

                    if (res.getResponse().getStatus() == 201) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {}
            });
        }

        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(1, successCount.get(), "Exactly ONE user registration should succeed with duplicate mobile in race condition");
    }
}
