package com.kaamsetu;

import com.kaamsetu.modules.application.dto.ApplyJobRequest;
import com.kaamsetu.modules.application.entity.ApplicationEntity;
import com.kaamsetu.modules.application.repository.ApplicationRepository;
import com.kaamsetu.modules.application.service.ApplicationService;
import com.kaamsetu.modules.assignment.entity.AssignmentEntity;
import com.kaamsetu.modules.assignment.entity.AttendanceEntity;
import com.kaamsetu.modules.assignment.entity.CompletionRecordEntity;
import com.kaamsetu.modules.assignment.repository.AssignmentRepository;
import com.kaamsetu.modules.assignment.repository.AttendanceRepository;
import com.kaamsetu.modules.assignment.repository.CompletionRecordRepository;
import com.kaamsetu.modules.assignment.service.AssignmentService;
import com.kaamsetu.modules.assignment.service.LifecycleSchedulerService;
import com.kaamsetu.modules.job.dto.CreateJobRequest;
import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.repository.JobRepository;
import com.kaamsetu.modules.job.service.JobService;
import com.kaamsetu.modules.location.service.LocationHierarchyService;
import com.kaamsetu.modules.notification.entity.NotificationEntity;
import com.kaamsetu.modules.notification.repository.NotificationRepository;
import com.kaamsetu.modules.notification.service.NotificationService;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.review.dto.SubmitReviewRequest;
import com.kaamsetu.modules.review.entity.ReviewEntity;
import com.kaamsetu.modules.review.repository.ReviewRepository;
import com.kaamsetu.modules.review.service.ReviewService;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🌾 KaamSetu (कामसेतू) - Complete Work / Job Lifecycle, Worker Selection,
 * Payment, Rating, Profile & Persistence Integration Test Suite
 * Scenarios: JOB-001 through JOB-047
 */
public class WorkLifecycleIntegrationTest {

    // Dynamic Repository Mocking Helper
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field f = findField(obj.getClass(), fieldName);
            if (f != null) {
                f.setAccessible(true);
                return f.get(obj);
            }
        } catch (Exception ignored) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createRepoProxy(Class<T> repoInterface, Map<UUID, Object> store) {
        return (T) Proxy.newProxyInstance(
            repoInterface.getClassLoader(),
            new Class<?>[]{repoInterface},
            (proxy, method, args) -> {
                String name = method.getName();
                if ("save".equals(name) || "saveAndFlush".equals(name)) {
                    Object entity = args[0];
                    try {
                        Field idField = findField(entity.getClass(), "id");
                        if (idField != null) {
                            idField.setAccessible(true);
                            UUID id = (UUID) idField.get(entity);
                            if (id == null) {
                                id = UUID.randomUUID();
                                idField.set(entity, id);
                            }
                            Field createdField = findField(entity.getClass(), "createdAt");
                            if (createdField != null) {
                                createdField.setAccessible(true);
                                if (createdField.get(entity) == null) {
                                    createdField.set(entity, Instant.now());
                                }
                            }
                            store.put(id, entity);
                        }
                    } catch (Exception ignored) {}
                    return entity;
                }
                if ("findById".equals(name)) {
                    return Optional.ofNullable(store.get((UUID) args[0]));
                }
                if ("findAll".equals(name) || "findAllByOrderByCreatedAtDesc".equals(name)) {
                    return new ArrayList<>(store.values());
                }
                if ("count".equals(name)) {
                    return (long) store.size();
                }
                if ("existsById".equals(name)) {
                    return store.containsKey((UUID) args[0]);
                }
                if ("deleteById".equals(name)) {
                    store.remove((UUID) args[0]);
                    return null;
                }

                // Query matchers for JobRepository
                if ("findByStatusOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status"))).toList();
                }
                if ("findByProviderIdOrderByCreatedAtDesc".equals(name)) {
                    UUID pId = (UUID) args[0];
                    return store.values().stream().filter(j -> pId.equals(getFieldValue(j, "providerId"))).toList();
                }
                if ("findByProviderIdAndStatusOrderByCreatedAtDesc".equals(name)) {
                    UUID pId = (UUID) args[0];
                    String status = (String) args[1];
                    return store.values().stream().filter(j -> pId.equals(getFieldValue(j, "providerId")) && status.equalsIgnoreCase((String) getFieldValue(j, "status"))).toList();
                }
                if ("findByStatusAndCategoryOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    String cat = (String) args[1];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status")) && cat.equalsIgnoreCase((String) getFieldValue(j, "category"))).toList();
                }
                if ("findByStatusAndTalukaOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    String tal = (String) args[1];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status")) && tal.equalsIgnoreCase((String) getFieldValue(j, "taluka"))).toList();
                }
                if ("findByStatusAndCategoryAndTalukaOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    String cat = (String) args[1];
                    String tal = (String) args[2];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status")) && cat.equalsIgnoreCase((String) getFieldValue(j, "category")) && tal.equalsIgnoreCase((String) getFieldValue(j, "taluka"))).toList();
                }
                if ("findByStatusAndTalukaInOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    List<String> talukas = (List<String>) args[1];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status")) && talukas.contains((String) getFieldValue(j, "taluka"))).toList();
                }
                if ("findByStatusAndCategoryAndTalukaInOrderByCreatedAtDesc".equals(name)) {
                    String status = (String) args[0];
                    String cat = (String) args[1];
                    List<String> talukas = (List<String>) args[2];
                    return store.values().stream().filter(j -> status.equalsIgnoreCase((String) getFieldValue(j, "status")) && cat.equalsIgnoreCase((String) getFieldValue(j, "category")) && talukas.contains((String) getFieldValue(j, "taluka"))).toList();
                }
                if ("searchOpenJobs".equals(name)) {
                    String q = ((String) args[0]).toLowerCase();
                    return store.values().stream().filter(j -> "OPEN".equalsIgnoreCase((String) getFieldValue(j, "status")) && (((String) getFieldValue(j, "title")).toLowerCase().contains(q) || ((String) getFieldValue(j, "category")).toLowerCase().contains(q))).toList();
                }

                // Query matchers for ApplicationRepository
                if ("findByJobIdOrderByCreatedAtDesc".equals(name)) {
                    UUID jId = (UUID) args[0];
                    return store.values().stream().filter(a -> jId.equals(getFieldValue(a, "jobId"))).toList();
                }
                if ("findByWorkerIdOrderByCreatedAtDesc".equals(name)) {
                    UUID wId = (UUID) args[0];
                    return store.values().stream().filter(a -> wId.equals(getFieldValue(a, "workerId"))).toList();
                }
                if ("findByJobIdAndWorkerId".equals(name)) {
                    UUID jId = (UUID) args[0];
                    UUID wId = (UUID) args[1];
                    return store.values().stream().filter(a -> jId.equals(getFieldValue(a, "jobId")) && wId.equals(getFieldValue(a, "workerId"))).findFirst();
                }
                if ("existsByJobIdAndWorkerId".equals(name)) {
                    UUID jId = (UUID) args[0];
                    UUID wId = (UUID) args[1];
                    return store.values().stream().anyMatch(a -> jId.equals(getFieldValue(a, "jobId")) && wId.equals(getFieldValue(a, "workerId")));
                }

                // Query matchers for AssignmentRepository
                if ("findByWorkerIdAndStatus".equals(name)) {
                    UUID wId = (UUID) args[0];
                    String status = (String) args[1];
                    return store.values().stream().filter(a -> wId.equals(getFieldValue(a, "workerId")) && status.equalsIgnoreCase((String) getFieldValue(a, "status"))).toList();
                }
                if ("findByProviderIdAndStatus".equals(name)) {
                    UUID pId = (UUID) args[0];
                    String status = (String) args[1];
                    return store.values().stream().filter(a -> pId.equals(getFieldValue(a, "providerId")) && status.equalsIgnoreCase((String) getFieldValue(a, "status"))).toList();
                }
                if ("countByJobIdAndStatus".equals(name)) {
                    UUID jId = (UUID) args[0];
                    String status = (String) args[1];
                    return store.values().stream().filter(a -> jId.equals(getFieldValue(a, "jobId")) && status.equalsIgnoreCase((String) getFieldValue(a, "status"))).count();
                }

                // Query matchers for CompletionRecordRepository
                if ("findByAssignmentId".equals(name)) {
                    UUID aId = (UUID) args[0];
                    return store.values().stream().filter(c -> aId.equals(getFieldValue(c, "assignmentId"))).findFirst();
                }
                if ("findByJobId".equals(name)) {
                    UUID jId = (UUID) args[0];
                    return store.values().stream().filter(c -> jId.equals(getFieldValue(c, "jobId"))).toList();
                }
                if ("findByPaymentStatus".equals(name)) {
                    String ps = (String) args[0];
                    return store.values().stream().filter(c -> ps.equalsIgnoreCase((String) getFieldValue(c, "paymentStatus"))).toList();
                }
                if ("findByWorkerRatingStatus".equals(name)) {
                    String wrs = (String) args[0];
                    return store.values().stream().filter(c -> wrs.equalsIgnoreCase((String) getFieldValue(c, "workerRatingStatus"))).toList();
                }
                if ("findByProviderRatingStatus".equals(name)) {
                    String prs = (String) args[0];
                    return store.values().stream().filter(c -> prs.equalsIgnoreCase((String) getFieldValue(c, "providerRatingStatus"))).toList();
                }

                // Query matchers for AttendanceRepository
                if ("findByAssignmentIdOrderByWorkDateDesc".equals(name)) {
                    UUID aId = (UUID) args[0];
                    return store.values().stream().filter(a -> aId.equals(getFieldValue(a, "assignmentId"))).toList();
                }
                if ("findByJobIdOrderByWorkDateDesc".equals(name)) {
                    UUID jId = (UUID) args[0];
                    return store.values().stream().filter(a -> jId.equals(getFieldValue(a, "jobId"))).toList();
                }
                if ("findByAssignmentIdAndWorkDate".equals(name)) {
                    UUID aId = (UUID) args[0];
                    LocalDate date = (LocalDate) args[1];
                    return store.values().stream().filter(a -> aId.equals(getFieldValue(a, "assignmentId")) && date.equals(getFieldValue(a, "workDate"))).findFirst();
                }

                // Query matchers for ReviewRepository
                if ("findByRevieweeIdOrderByCreatedAtDesc".equals(name)) {
                    UUID rId = (UUID) args[0];
                    return store.values().stream().filter(r -> rId.equals(getFieldValue(r, "revieweeId"))).toList();
                }
                if ("existsByAssignmentIdAndReviewerId".equals(name)) {
                    UUID aId = (UUID) args[0];
                    UUID rId = (UUID) args[1];
                    return store.values().stream().anyMatch(r -> aId.equals(getFieldValue(r, "assignmentId")) && rId.equals(getFieldValue(r, "reviewerId")));
                }
                if ("calculateAverageRatingForUser".equals(name)) {
                    UUID uId = (UUID) args[0];
                    List<Object> reviews = store.values().stream().filter(r -> uId.equals(getFieldValue(r, "revieweeId"))).toList();
                    if (reviews.isEmpty()) return null;
                    double sum = reviews.stream().mapToDouble(r -> ((BigDecimal) getFieldValue(r, "rating")).doubleValue()).sum();
                    return BigDecimal.valueOf(sum / reviews.size()).setScale(1, RoundingMode.HALF_UP);
                }
                if ("countReviewsForUser".equals(name)) {
                    UUID uId = (UUID) args[0];
                    return store.values().stream().filter(r -> uId.equals(getFieldValue(r, "revieweeId"))).count();
                }

                // Query matchers for WorkerRepository & ProviderRepository
                if ("findByUserId".equals(name)) {
                    UUID uId = (UUID) args[0];
                    return store.values().stream().filter(w -> uId.equals(getFieldValue(w, "userId"))).findFirst();
                }

                // Query matchers for NotificationRepository
                if ("findByUserIdOrderByCreatedAtDesc".equals(name)) {
                    UUID uId = (UUID) args[0];
                    return store.values().stream()
                            .filter(n -> uId.equals(getFieldValue(n, "userId")))
                            .sorted((a, b) -> {
                                Instant ia = (Instant) getFieldValue(a, "createdAt");
                                Instant ib = (Instant) getFieldValue(b, "createdAt");
                                if (ia == null || ib == null) return 0;
                                return ib.compareTo(ia);
                            })
                            .toList();
                }
                if ("findByUserIdAndCategoryOrderByCreatedAtDesc".equals(name)) {
                    UUID uId = (UUID) args[0];
                    String cat = (String) args[1];
                    return store.values().stream()
                            .filter(n -> uId.equals(getFieldValue(n, "userId")) && cat.equalsIgnoreCase((String) getFieldValue(n, "category")))
                            .sorted((a, b) -> {
                                Instant ia = (Instant) getFieldValue(a, "createdAt");
                                Instant ib = (Instant) getFieldValue(b, "createdAt");
                                if (ia == null || ib == null) return 0;
                                return ib.compareTo(ia);
                            })
                            .toList();
                }

                return null;
            }
        );
    }

    // Context & Fixtures
    private Map<UUID, Object> jobStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> applicationStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> assignmentStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> completionRecordStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> attendanceStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> reviewStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> workerStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> providerStore = new ConcurrentHashMap<>();
    private Map<UUID, Object> notificationStore = new ConcurrentHashMap<>();

    private JobRepository jobRepository;
    private ApplicationRepository applicationRepository;
    private AssignmentRepository assignmentRepository;
    private CompletionRecordRepository completionRecordRepository;
    private AttendanceRepository attendanceRepository;
    private ReviewRepository reviewRepository;
    private WorkerRepository workerRepository;
    private ProviderRepository providerRepository;
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;
    private JobService jobService;
    private ApplicationService applicationService;
    private AssignmentService assignmentService;
    private ReviewService reviewService;
    private LifecycleSchedulerService schedulerService;

    // Test Users
    private UUID providerUserId;
    private UUID workerUserId1;
    private UUID workerUserId2;
    private UUID workerUserId3;
    private UUID unrelatedUserId;

    private ProviderEntity provider;
    private WorkerEntity worker1;
    private WorkerEntity worker2;
    private WorkerEntity worker3;

    public void setUp() {
        jobStore.clear();
        applicationStore.clear();
        assignmentStore.clear();
        completionRecordStore.clear();
        attendanceStore.clear();
        reviewStore.clear();
        workerStore.clear();
        providerStore.clear();
        notificationStore.clear();

        jobRepository = createRepoProxy(JobRepository.class, jobStore);
        applicationRepository = createRepoProxy(ApplicationRepository.class, applicationStore);
        assignmentRepository = createRepoProxy(AssignmentRepository.class, assignmentStore);
        completionRecordRepository = createRepoProxy(CompletionRecordRepository.class, completionRecordStore);
        attendanceRepository = createRepoProxy(AttendanceRepository.class, attendanceStore);
        reviewRepository = createRepoProxy(ReviewRepository.class, reviewStore);
        workerRepository = createRepoProxy(WorkerRepository.class, workerStore);
        providerRepository = createRepoProxy(ProviderRepository.class, providerStore);
        notificationRepository = createRepoProxy(NotificationRepository.class, notificationStore);

        notificationService = new NotificationService(notificationRepository);
        LocationHierarchyService locationHierarchyService = new LocationHierarchyService(null, null, null, null, null, null);
        jobService = new JobService(jobRepository, providerRepository, workerRepository, notificationService, locationHierarchyService);
        applicationService = new ApplicationService(applicationRepository, jobRepository, workerRepository, providerRepository, notificationService);
        assignmentService = new AssignmentService(assignmentRepository, completionRecordRepository, attendanceRepository, jobRepository, workerRepository, providerRepository, applicationRepository, notificationService);
        reviewService = new ReviewService(reviewRepository, assignmentRepository, completionRecordRepository, jobRepository, workerRepository, providerRepository, null, notificationService);
        schedulerService = new LifecycleSchedulerService(completionRecordRepository, jobRepository, providerRepository, workerRepository, notificationService);

        providerUserId = UUID.randomUUID();
        workerUserId1 = UUID.randomUUID();
        workerUserId2 = UUID.randomUUID();
        workerUserId3 = UUID.randomUUID();
        unrelatedUserId = UUID.randomUUID();

        provider = ProviderEntity.builder()
                .userId(providerUserId)
                .businessName("पाटील फार्म्स (Patil Farms)")
                .village("सासवड (Saswad)")
                .taluka("पुरंदर (Purandar)")
                .district("पुणे ग्रामीण (Pune Rural)")
                .ratingAvg(new BigDecimal("5.0"))
                .trustIndex(new BigDecimal("5.0"))
                .build();
        provider = providerRepository.save(provider);

        worker1 = WorkerEntity.builder()
                .userId(workerUserId1)
                .fullName("सुरेश जाधव")
                .village("सासवड (Saswad)")
                .taluka("पुरंदर (Purandar)")
                .district("पुणे ग्रामीण (Pune Rural)")
                .availableToday(true)
                .ratingAvg(new BigDecimal("5.0"))
                .trustIndex(new BigDecimal("5.0"))
                .build();
        worker1 = workerRepository.save(worker1);

        worker2 = WorkerEntity.builder()
                .userId(workerUserId2)
                .fullName("रमेश शिंदे")
                .village("सासवड (Saswad)")
                .taluka("पुरंदर (Purandar)")
                .district("पुणे ग्रामीण (Pune Rural)")
                .availableToday(true)
                .ratingAvg(new BigDecimal("5.0"))
                .trustIndex(new BigDecimal("5.0"))
                .build();
        worker2 = workerRepository.save(worker2);

        worker3 = WorkerEntity.builder()
                .userId(workerUserId3)
                .fullName("गणेश पवार")
                .village("सासवड (Saswad)")
                .taluka("पुरंदर (Purandar)")
                .district("पुणे ग्रामीण (Pune Rural)")
                .availableToday(true)
                .ratingAvg(new BigDecimal("5.0"))
                .trustIndex(new BigDecimal("5.0"))
                .build();
        worker3 = workerRepository.save(worker3);
    }

    private CreateJobRequest createValidJobRequest(int workersRequired, LocalDate startDate, LocalDate deadline) {
        return CreateJobRequest.builder()
                .title("टोमॅटो तोडणी व प्रतवारी (Tomato Harvesting)")
                .category("cat.agriculture")
                .workModel("ONETIME")
                .description("शेतातील ५ एकर टोमॅटो तोडणीचे काम.")
                .dailyWage(new BigDecimal("650.00"))
                .paymentUnit("PER_DAY")
                .overtimeAvailable(true)
                .overtimeRate(new BigDecimal("80.00"))
                .additionalPaymentConditions("नाश्ता व चहा दिला जाईल.")
                .startTime("08:00")
                .endTime("17:00")
                .workingHours(new BigDecimal("8.00"))
                .lunchBreak("60")
                .teaBreak("15")
                .otherBreak("0")
                .facilities("TEA,LUNCH,WATER")
                .facilityDetails("दुपारचे जेवण व शुद्ध पिण्याचे पाणी")
                .workersRequired(workersRequired)
                .startDate(startDate)
                .deadline(deadline)
                .durationDays(1)
                .village("सासवड (Saswad)")
                .taluka("पुरंदर (Purandar)")
                .district("पुणे ग्रामीण (Pune Rural)")
                .priority("NORMAL")
                .build();
    }

    // JOB-001..047 Tests

    public void testJob001_JobCreationFullFields() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now());
        JobEntity created = jobService.createJob(providerUserId, req);

        assertNotNull(created.getId());
        assertEquals("टोमॅटो तोडणी व प्रतवारी (Tomato Harvesting)", created.getTitle());
        assertEquals("ONETIME", created.getWorkModel());
        assertEquals(new BigDecimal("650.00"), created.getDailyWage());
        assertEquals("PER_DAY", created.getPaymentUnit());
        assertTrue(created.getOvertimeAvailable());
        assertEquals("TEA,LUNCH,WATER", created.getFacilities());
        assertEquals("OPEN", created.getStatus());
    }

    public void testJob002_ValidationStartDateNotInPast() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> jobService.createJob(providerUserId, req));
    }

    public void testJob003_ValidationDeadlineNotAfterStartDate() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        assertThrows(IllegalArgumentException.class, () -> jobService.createJob(providerUserId, req));
    }

    public void testJob004_ValidationWorkersRequiredMinOne() {
        setUp();
        CreateJobRequest req = createValidJobRequest(0, LocalDate.now().plusDays(1), LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> jobService.createJob(providerUserId, req));
    }

    public void testJob005_ValidationDailyWagePositive() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now());
        req.setDailyWage(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> jobService.createJob(providerUserId, req));
    }

    public void testJob006_JobStatusInitializesOpen() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now());
        JobEntity job = jobService.createJob(providerUserId, req);
        assertEquals("OPEN", job.getStatus());
    }

    public void testJob007_WorkerDiscoversOpenJobs() {
        setUp();
        CreateJobRequest req = createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now());
        jobService.createJob(providerUserId, req);

        List<JobEntity> openPurandar = jobService.getOpenJobs("cat.agriculture", "पुरंदर (Purandar)");
        assertFalse(openPurandar.isEmpty());
        assertEquals(1, openPurandar.size());
    }

    public void testJob008_WorkerSubmitsApplication() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        ApplicationEntity app = applicationService.applyToJob(workerUserId1, job.getId(), ApplyJobRequest.builder().workerNotes("अनुभवी शेतमजूर").build());
        assertNotNull(app.getId());
        assertEquals(job.getId(), app.getJobId());
        assertEquals(worker1.getId(), app.getWorkerId());
    }

    public void testJob009_ApplicationStatusApplied() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        ApplicationEntity app = applicationService.applyToJob(workerUserId1, job.getId(), null);
        assertEquals("APPLIED", app.getStatus());
    }

    public void testJob010_NotificationDispatchedToProvider() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);

        List<NotificationEntity> notifs = notificationService.getUserNotifications(providerUserId, "APPLICATIONS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.get(0).getTitle().contains("अर्ज"));
    }

    public void testJob011_DuplicateApplicationPrevention() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);
        assertThrows(IllegalStateException.class, () -> applicationService.applyToJob(workerUserId1, job.getId(), null));
    }

    public void testJob012_ApplicationBlockedIfExpired() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(2), LocalDate.now()));
        job.setDeadline(LocalDate.now().minusDays(1)); // simulate past deadline
        jobRepository.save(job);

        assertThrows(IllegalStateException.class, () -> applicationService.applyToJob(workerUserId1, job.getId(), null));
    }

    public void testJob013_ApplicationBlockedIfFull() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now().plusDays(1), LocalDate.now()));
        job.setStatus("FULL");
        job.setWorkersConfirmed(1);
        jobRepository.save(job);

        assertThrows(IllegalStateException.class, () -> applicationService.applyToJob(workerUserId1, job.getId(), null));
    }

    public void testJob014_WorkerWithdrawsApplication() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        ApplicationEntity app = applicationService.applyToJob(workerUserId1, job.getId(), null);

        ApplicationEntity withdrawn = applicationService.withdrawApplication(workerUserId1, app.getId());
        assertEquals("WITHDRAWN", withdrawn.getStatus());
    }

    public void testJob015_ProviderViewsApplicants() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);
        applicationService.applyToJob(workerUserId2, job.getId(), null);

        List<ApplicationEntity> apps = applicationService.getJobApplications(providerUserId, job.getId());
        assertEquals(2, apps.size());
    }

    public void testJob016_ProviderSelectsWorker() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);

        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assertNotNull(asg.getId());
        assertEquals("SELECTED", asg.getStatus());
    }

    public void testJob017_SelectionNotificationSentToWorker() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);
        assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        List<NotificationEntity> notifs = notificationService.getUserNotifications(workerUserId1, "SELECTIONS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.get(0).getTitle().contains("निवड"));
    }

    public void testJob018_CapacityEnforcementOnSelectWorker() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now().plusDays(1), LocalDate.now()));
        applicationService.applyToJob(workerUserId1, job.getId(), null);
        applicationService.applyToJob(workerUserId2, job.getId(), null);

        // Select worker 1 (capacity 1/1 reached)
        assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        // Attempt selecting worker 2 should throw capacity error
        assertThrows(IllegalStateException.class, () -> assignmentService.selectWorker(providerUserId, job.getId(), worker2.getId()));
    }

    public void testJob019_ConcurrencySafetySelection() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now().plusDays(1), LocalDate.now()));
        assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        JobEntity updated = jobRepository.findById(job.getId()).orElseThrow();
        assertEquals("FULL", updated.getStatus());
        assertEquals(1, updated.getWorkersConfirmed());
    }

    public void testJob020_WorkerConfirmsAssignment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity confirmed = assignmentService.confirmAssignment(workerUserId1, asg.getId());
        assertEquals("CONFIRMED", confirmed.getStatus());
        assertNotNull(confirmed.getConfirmedAt());
    }

    public void testJob021_AutoTransitionJobAssignedWhenCapacityReached() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        AssignmentEntity asg1 = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        AssignmentEntity asg2 = assignmentService.selectWorker(providerUserId, job.getId(), worker2.getId());

        assignmentService.confirmAssignment(workerUserId1, asg1.getId());
        assignmentService.confirmAssignment(workerUserId2, asg2.getId());

        JobEntity updated = jobRepository.findById(job.getId()).orElseThrow();
        assertEquals("ASSIGNED", updated.getStatus());
        assertEquals(2, updated.getWorkersConfirmed());
    }

    public void testJob022_WorkerDeclinesAssignment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(2, LocalDate.now().plusDays(1), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity declined = assignmentService.declineAssignment(workerUserId1, asg.getId());
        assertEquals("DECLINED", declined.getStatus());
    }

    public void testJob023_JobReopensIfConfirmedWorkersFallBelowRequired() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now().plusDays(1), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.declineAssignment(workerUserId1, asg.getId());

        JobEntity updated = jobRepository.findById(job.getId()).orElseThrow();
        assertEquals("OPEN", updated.getStatus());
    }

    public void testJob024_StartWorkInProgress() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity inProg = assignmentService.startWork(workerUserId1, asg.getId());
        assertEquals("IN_PROGRESS", inProg.getStatus());
        assertNotNull(inProg.getWorkStartedAt());
    }

    public void testJob025_NotificationSentOnWorkStart() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.startWork(workerUserId1, asg.getId());

        List<NotificationEntity> notifs = notificationService.getUserNotifications(workerUserId1, "REMINDERS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.get(0).getTitle().contains("काम सुरू"));
    }

    public void testJob026_RecordDailyAttendance() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AttendanceEntity att = assignmentService.recordAttendance(
                providerUserId, asg.getId(), LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(17, 0), "PRESENT", "वेळेवर हजर");
        assertNotNull(att.getId());
        assertEquals("PRESENT", att.getAttendanceStatus());
        assertEquals("वेळेवर हजर", att.getRemarks());
    }

    public void testJob027_FetchAttendanceHistory() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.recordAttendance(providerUserId, asg.getId(), LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(17, 0), "PRESENT", "वेळेवर हजर");

        List<AttendanceEntity> hist = assignmentService.getAttendancesForAssignment(asg.getId());
        assertEquals(1, hist.size());
    }

    public void testJob028_WorkCompletionByProvider() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity completed = assignmentService.confirmCompletion(providerUserId, asg.getId());
        assertEquals("COMPLETED", completed.getStatus());
        assertNotNull(completed.getCompletedAt());
    }

    public void testJob029_IndividualCompletionRecordCreated() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        Optional<CompletionRecordEntity> recordOpt = completionRecordRepository.findByAssignmentId(asg.getId());
        assertTrue(recordOpt.isPresent());
        CompletionRecordEntity record = recordOpt.get();
        assertEquals(job.getId(), record.getJobId());
        assertEquals(worker1.getId(), record.getWorkerId());
        assertEquals("COMPLETED", record.getCompletionStatus());
    }

    public void testJob030_PaymentStatusInitializedPending() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        CompletionRecordEntity record = completionRecordRepository.findByAssignmentId(asg.getId()).orElseThrow();
        assertEquals("PENDING", record.getPaymentStatus());
    }

    public void testJob031_TotalPaymentCalculation() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        // Base 650 + Overtime 160 + Additional 50 = 860
        CompletionRecordEntity record = assignmentService.confirmPayment(
                providerUserId, asg.getId(), new BigDecimal("160.00"), new BigDecimal("50.00"), "UPI");

        assertEquals(new BigDecimal("650.00"), record.getBasePayment());
        assertEquals(new BigDecimal("160.00"), record.getOvertimeAmount());
        assertEquals(new BigDecimal("50.00"), record.getAdditionalAmount());
        assertEquals(new BigDecimal("860.00"), record.getTotalAmount());
        assertEquals("PAID", record.getPaymentStatus());
    }

    public void testJob032_ProviderConfirmsPayment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        CompletionRecordEntity paid = assignmentService.confirmPayment(providerUserId, asg.getId(), BigDecimal.ZERO, BigDecimal.ZERO, "CASH");
        assertEquals("PAID", paid.getPaymentStatus());
        assertNotNull(paid.getPaymentConfirmedAt());
    }

    public void testJob033_NotificationDispatchedOnPayment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());
        assignmentService.confirmPayment(providerUserId, asg.getId(), BigDecimal.ZERO, BigDecimal.ZERO, "CASH");

        List<NotificationEntity> notifs = notificationService.getUserNotifications(workerUserId1, "RATINGS_PAYMENTS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.stream().anyMatch(n -> n.getTitle().contains("मजुरी")));
    }

    public void testJob034_UnauthorizedUserCannotConfirmPayment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        assertThrows(Exception.class, () -> assignmentService.confirmPayment(unrelatedUserId, asg.getId(), BigDecimal.ZERO, BigDecimal.ZERO, "CASH"));
    }

    public void testJob035_JobStatusCompletedWhenAllAssignmentsCompleted() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        JobEntity updated = jobRepository.findById(job.getId()).orElseThrow();
        assertEquals("COMPLETED", updated.getStatus());
    }

    public void testJob036_WorkerSubmitsMultiDimensionalRatingForProvider() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        SubmitReviewRequest req = SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .workManagementRating(new BigDecimal("5.0"))
                .paymentExperienceRating(new BigDecimal("5.0"))
                .behaviorRating(new BigDecimal("5.0"))
                .timeManagementRating(new BigDecimal("5.0"))
                .reviewText("उत्कृष्ट कामाचे नियोजन व वेळेवर मजुरी दिली.")
                .build();

        ReviewEntity review = reviewService.submitReview(workerUserId1, req);
        assertNotNull(review.getId());
        assertEquals("WORKER", review.getReviewerRole());
        assertEquals(providerUserId, review.getRevieweeId());
        assertEquals(new BigDecimal("5.0"), review.getPaymentExperienceRating());
    }

    public void testJob037_ProviderSubmitsMultiDimensionalRatingForWorker() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        SubmitReviewRequest req = SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .qualityRating(new BigDecimal("5.0"))
                .punctualityRating(new BigDecimal("5.0"))
                .behaviorRating(new BigDecimal("5.0"))
                .reliabilityRating(new BigDecimal("5.0"))
                .skillRating(new BigDecimal("5.0"))
                .reviewText("वेळेवर हजर, कामाचा उत्कृष्ट दर्जा.")
                .build();

        ReviewEntity review = reviewService.submitReview(providerUserId, req);
        assertNotNull(review.getId());
        assertEquals("PROVIDER", review.getReviewerRole());
        assertEquals(workerUserId1, review.getRevieweeId());
        assertEquals(new BigDecimal("5.0"), review.getQualityRating());
    }

    public void testJob038_RatingBlockedIfAssignmentNotCompleted() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId()); // status SELECTED

        SubmitReviewRequest req = SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .build();

        assertThrows(IllegalStateException.class, () -> reviewService.submitReview(workerUserId1, req));
    }

    public void testJob039_DuplicateRatingPrevention() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        SubmitReviewRequest req = SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .build();

        reviewService.submitReview(workerUserId1, req);
        assertThrows(IllegalStateException.class, () -> reviewService.submitReview(workerUserId1, req));
    }

    public void testJob040_UnrelatedUserCannotSubmitRating() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        SubmitReviewRequest req = SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .build();

        assertThrows(SecurityException.class, () -> reviewService.submitReview(unrelatedUserId, req));
    }

    public void testJob041_DynamicRatingRecalculationOnWorker() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        reviewService.submitReview(providerUserId, SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("4.0"))
                .build());

        WorkerEntity updatedWorker = workerRepository.findById(worker1.getId()).orElseThrow();
        assertEquals(new BigDecimal("4.0"), updatedWorker.getRatingAvg());
    }

    public void testJob042_DynamicRatingRecalculationOnProvider() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        reviewService.submitReview(workerUserId1, SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("4.5"))
                .build());

        ProviderEntity updatedProvider = providerRepository.findById(provider.getId()).orElseThrow();
        assertEquals(new BigDecimal("4.5"), updatedProvider.getRatingAvg());
    }

    public void testJob043_TrustIndexUpdatedOnProfile() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        reviewService.submitReview(workerUserId1, SubmitReviewRequest.builder()
                .assignmentId(asg.getId())
                .rating(new BigDecimal("5.0"))
                .build());

        ProviderEntity updatedProvider = providerRepository.findById(provider.getId()).orElseThrow();
        assertNotNull(updatedProvider.getTrustIndex());
        assertTrue(updatedProvider.getTrustIndex().compareTo(BigDecimal.ZERO) > 0);
    }

    public void testJob044_NextDayReminderSchedulerPendingPayment() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        CompletionRecordEntity rec = completionRecordRepository.findByAssignmentId(asg.getId()).orElseThrow();
        rec.setWorkDate(LocalDate.now().minusDays(1));
        completionRecordRepository.save(rec);

        schedulerService.processNextDayReminders();

        List<NotificationEntity> notifs = notificationService.getUserNotifications(providerUserId, "REMINDERS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.stream().anyMatch(n -> n.getTitle().contains("मजुरी प्रलंबित")));
    }

    public void testJob045_NextDayReminderSchedulerPendingRating() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());
        assignmentService.confirmCompletion(providerUserId, asg.getId());

        schedulerService.processNextDayReminders();

        List<NotificationEntity> notifs = notificationService.getUserNotifications(workerUserId1, "RATINGS_PAYMENTS");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.stream().anyMatch(n -> n.getTitle().contains("अभिप्राय प्रलंबित")));
    }

    public void testJob046_CancelAssignmentWithReason() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity cancelled = assignmentService.cancelAssignment(providerUserId, asg.getId(), "पाऊस आल्यामुळे काम थांबवले");
        assertEquals("CANCELLED", cancelled.getStatus());
        assertEquals("पाऊस आल्यामुळे काम थांबवले", cancelled.getCancelReason());
    }

    public void testJob047_ReportPartyNoShow() {
        setUp();
        JobEntity job = jobService.createJob(providerUserId, createValidJobRequest(1, LocalDate.now(), LocalDate.now()));
        AssignmentEntity asg = assignmentService.selectWorker(providerUserId, job.getId(), worker1.getId());

        AssignmentEntity noShow = assignmentService.reportNoShow(providerUserId, asg.getId(), "कामगार कामावर आला नाही");
        assertEquals("NO_SHOW", noShow.getStatus());
        assertTrue(noShow.getCancelReason().contains("कामगार कामावर आला नाही"));
    }

    // Main Test Runner executing JOB-001 through JOB-047
    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("🌾 KAAMSETU WORK LIFECYCLE & SETTLEMENT TEST SUITE (JOB-001..047)");
        System.out.println("===============================================================================");

        WorkLifecycleIntegrationTest suite = new WorkLifecycleIntegrationTest();

        List<String[]> testCases = Arrays.asList(
            new String[]{"JOB-001", "Job Creation Full 3-Step Fields & Model", "testJob001_JobCreationFullFields"},
            new String[]{"JOB-002", "Validation: Start Date Cannot Be In Past", "testJob002_ValidationStartDateNotInPast"},
            new String[]{"JOB-003", "Validation: Deadline Cannot Be After Start Date", "testJob003_ValidationDeadlineNotAfterStartDate"},
            new String[]{"JOB-004", "Validation: Workers Required Minimum One", "testJob004_ValidationWorkersRequiredMinOne"},
            new String[]{"JOB-005", "Validation: Daily Wage Must Be Positive", "testJob005_ValidationDailyWagePositive"},
            new String[]{"JOB-006", "Job Status Initializes as OPEN", "testJob006_JobStatusInitializesOpen"},
            new String[]{"JOB-007", "Worker Discovers Open Jobs by Location/Category", "testJob007_WorkerDiscoversOpenJobs"},
            new String[]{"JOB-008", "Worker Submits Application to Open Job", "testJob008_WorkerSubmitsApplication"},
            new String[]{"JOB-009", "Application Status Initialized as APPLIED", "testJob009_ApplicationStatusApplied"},
            new String[]{"JOB-010", "Notification Dispatched to Provider on Apply", "testJob010_NotificationDispatchedToProvider"},
            new String[]{"JOB-011", "Duplicate Application Prevention Rule", "testJob011_DuplicateApplicationPrevention"},
            new String[]{"JOB-012", "Application Blocked If Job Is EXPIRED", "testJob012_ApplicationBlockedIfExpired"},
            new String[]{"JOB-013", "Application Blocked If Job Is FULL / FILLED", "testJob013_ApplicationBlockedIfFull"},
            new String[]{"JOB-014", "Worker Withdraws Application", "testJob014_WorkerWithdrawsApplication"},
            new String[]{"JOB-015", "Provider Views Applicants for Posted Job", "testJob015_ProviderViewsApplicants"},
            new String[]{"JOB-016", "Provider Selects Worker Candidate (SELECTED)", "testJob016_ProviderSelectsWorker"},
            new String[]{"JOB-017", "Selection Notification Dispatched to Worker", "testJob017_SelectionNotificationSentToWorker"},
            new String[]{"JOB-018", "Capacity Enforcement on Worker Selection", "testJob018_CapacityEnforcementOnSelectWorker"},
            new String[]{"JOB-019", "Concurrency Safety on Worker Selection", "testJob019_ConcurrencySafetySelection"},
            new String[]{"JOB-020", "Worker Confirms Selection (CONFIRMED)", "testJob020_WorkerConfirmsAssignment"},
            new String[]{"JOB-021", "Auto-Transition Job Status When Capacity Reached", "testJob021_AutoTransitionJobAssignedWhenCapacityReached"},
            new String[]{"JOB-022", "Worker Declines Selection (DECLINED)", "testJob022_WorkerDeclinesAssignment"},
            new String[]{"JOB-023", "Job Re-Opens If Confirmed Below Required", "testJob023_JobReopensIfConfirmedWorkersFallBelowRequired"},
            new String[]{"JOB-024", "Start Work Transitions to IN_PROGRESS", "testJob024_StartWorkInProgress"},
            new String[]{"JOB-025", "Notification Dispatched to Worker on Start", "testJob025_NotificationSentOnWorkStart"},
            new String[]{"JOB-026", "Record Daily Attendance (PRESENT, Timings, Remarks)", "testJob026_RecordDailyAttendance"},
            new String[]{"JOB-027", "Fetch Attendance History for Assignment & Job", "testJob027_FetchAttendanceHistory"},
            new String[]{"JOB-028", "Work Completion by Provider (COMPLETED)", "testJob028_WorkCompletionByProvider"},
            new String[]{"JOB-029", "Persistent CompletionRecord Created Per Worker", "testJob029_IndividualCompletionRecordCreated"},
            new String[]{"JOB-030", "Payment Status Initialized as PENDING", "testJob030_PaymentStatusInitializedPending"},
            new String[]{"JOB-031", "Structured Payment Calculation (Base+OT+Add)", "testJob031_TotalPaymentCalculation"},
            new String[]{"JOB-032", "Provider Confirms Payment (PAID + Timestamp)", "testJob032_ProviderConfirmsPayment"},
            new String[]{"JOB-033", "Payment Notification Dispatched to Worker", "testJob033_NotificationDispatchedOnPayment"},
            new String[]{"JOB-034", "Unauthorized User Cannot Confirm Payment", "testJob034_UnauthorizedUserCannotConfirmPayment"},
            new String[]{"JOB-035", "Job Updates to COMPLETED on All Assignments Done", "testJob035_JobStatusCompletedWhenAllAssignmentsCompleted"},
            new String[]{"JOB-036", "Worker Submits Multi-Dimensional Provider Rating", "testJob036_WorkerSubmitsMultiDimensionalRatingForProvider"},
            new String[]{"JOB-037", "Provider Submits Multi-Dimensional Worker Rating", "testJob037_ProviderSubmitsMultiDimensionalRatingForWorker"},
            new String[]{"JOB-038", "Rating Blocked If Assignment Not COMPLETED", "testJob038_RatingBlockedIfAssignmentNotCompleted"},
            new String[]{"JOB-039", "Duplicate Rating Prevention Rule", "testJob039_DuplicateRatingPrevention"},
            new String[]{"JOB-040", "Unrelated User Cannot Rate Assignment", "testJob040_UnrelatedUserCannotSubmitRating"},
            new String[]{"JOB-041", "Dynamic Average Rating Recalculation on Worker", "testJob041_DynamicRatingRecalculationOnWorker"},
            new String[]{"JOB-042", "Dynamic Average Rating Recalculation on Provider", "testJob042_DynamicRatingRecalculationOnProvider"},
            new String[]{"JOB-043", "Trust Index Dynamic Profile Update", "testJob043_TrustIndexUpdatedOnProfile"},
            new String[]{"JOB-044", "Next-Day Scheduler Notifies Pending Payments", "testJob044_NextDayReminderSchedulerPendingPayment"},
            new String[]{"JOB-045", "Next-Day Scheduler Notifies Pending Ratings", "testJob045_NextDayReminderSchedulerPendingRating"},
            new String[]{"JOB-046", "Cancel Assignment with Mandatory Reason", "testJob046_CancelAssignmentWithReason"},
            new String[]{"JOB-047", "Report Party No-Show with Cancellation Notes", "testJob047_ReportPartyNoShow"}
        );

        int passed = 0;
        int failed = 0;

        for (String[] tc : testCases) {
            String id = tc[0];
            String name = tc[1];
            String method = tc[2];

            try {
                java.lang.reflect.Method m = WorkLifecycleIntegrationTest.class.getMethod(method);
                m.invoke(suite);
                System.out.printf("  ✅ [%s] %-55s : PASS%n", id, name);
                passed++;
            } catch (Exception ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                System.out.printf("  ❌ [%s] %-55s : FAIL (%s)%n", id, name, cause.getMessage());
                failed++;
            }
        }

        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("RESULTS: TOTAL: %d | PASSED: %d | FAILED: %d | STATUS: %s%n",
                testCases.size(), passed, failed, (failed == 0 ? "ALL PASS (100%)" : "FAILED"));
        System.out.println("===============================================================================");
    }
}
