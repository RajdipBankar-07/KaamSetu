package com.kaamsetu.modules.assignment.service;

import com.kaamsetu.modules.application.entity.ApplicationEntity;
import com.kaamsetu.modules.application.repository.ApplicationRepository;
import com.kaamsetu.modules.assignment.entity.AssignmentEntity;
import com.kaamsetu.modules.assignment.entity.AttendanceEntity;
import com.kaamsetu.modules.assignment.entity.CompletionRecordEntity;
import com.kaamsetu.modules.assignment.repository.AssignmentRepository;
import com.kaamsetu.modules.assignment.repository.AttendanceRepository;
import com.kaamsetu.modules.assignment.repository.CompletionRecordRepository;
import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.repository.JobRepository;
import com.kaamsetu.modules.notification.service.NotificationService;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CompletionRecordRepository completionRecordRepository;
    private final AttendanceRepository attendanceRepository;
    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Transactional
    public synchronized AssignmentEntity selectWorker(UUID userId, UUID jobId, UUID workerId) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found for user: " + userId));

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!job.getProviderId().equals(provider.getId())) {
            throw new SecurityException("Unauthorized: You do not own this job");
        }

        // Check already confirmed/selected workers count
        List<AssignmentEntity> activeAssignments = assignmentRepository.findByJobIdOrderByCreatedAtDesc(jobId);
        long selectedCount = activeAssignments.stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()) && !"DECLINED".equalsIgnoreCase(a.getStatus()) && !"NO_SHOW".equalsIgnoreCase(a.getStatus()))
                .filter(a -> !a.getWorkerId().equals(workerId)) // exclude current worker if re-selecting
                .count();

        if (selectedCount >= job.getWorkersRequired()) {
            throw new IllegalStateException(String.format("Job capacity reached: Required %d, already selected %d workers",
                    job.getWorkersRequired(), selectedCount));
        }

        Optional<ApplicationEntity> appOpt = applicationRepository.findByJobIdAndWorkerId(jobId, workerId);
        UUID applicationId = appOpt.map(ApplicationEntity::getId).orElse(null);
        if (appOpt.isPresent()) {
            ApplicationEntity app = appOpt.get();
            app.setStatus("SELECTED");
            applicationRepository.save(app);
        }

        Optional<AssignmentEntity> existing = assignmentRepository.findByJobIdAndWorkerId(jobId, workerId);
        AssignmentEntity assignment;
        long timeoutHours = "URGENT".equalsIgnoreCase(job.getPriority()) ? 2 : 24;

        LocalDate startDate = job.getStartDate() != null ? job.getStartDate() : LocalDate.now();
        int duration = job.getDurationDays() != null && job.getDurationDays() > 0 ? job.getDurationDays() : 1;
        LocalDate expectedCompletion = startDate.plusDays(duration - 1);
        BigDecimal wage = job.getDailyWage() != null ? job.getDailyWage() : new BigDecimal("600.00");

        if (existing.isPresent()) {
            assignment = existing.get();
            assignment.setStatus("SELECTED");
            assignment.setStartDate(startDate);
            assignment.setExpectedCompletionDate(expectedCompletion);
            assignment.setAgreedWage(wage);
            assignment.setBasePayment(wage);
            assignment.setSelectionWindowExpiresAt(Instant.now().plus(timeoutHours, ChronoUnit.HOURS));
        } else {
            assignment = AssignmentEntity.builder()
                    .jobId(jobId)
                    .applicationId(applicationId)
                    .workerId(workerId)
                    .providerId(provider.getId())
                    .status("SELECTED")
                    .startDate(startDate)
                    .expectedCompletionDate(expectedCompletion)
                    .agreedWage(wage)
                    .basePayment(wage)
                    .selectionWindowExpiresAt(Instant.now().plus(timeoutHours, ChronoUnit.HOURS))
                    .paymentType("CASH")
                    .paymentStatus("PENDING")
                    .paymentConfirmedByWorker(false)
                    .build();
        }

        AssignmentEntity saved = assignmentRepository.save(assignment);

        // Update Job confirmed count & auto-FULL transition rule
        long newTotalSelected = selectedCount + 1;
        job.setWorkersConfirmed((int) newTotalSelected);
        if (newTotalSelected >= job.getWorkersRequired()) {
            job.setStatus("FULL");
        } else {
            job.setStatus("APPLICATIONS");
        }
        jobRepository.save(job);

        // Notify Worker
        try {
            WorkerEntity worker = workerRepository.findById(workerId).orElse(null);
            if (worker != null) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "SELECTIONS",
                        "🎉 कामगार निवड (Worker Selected)",
                        String.format("आपली \"%s\" या कामासाठी निवड झाली आहे. कामाची तारीख: %s", job.getTitle(), startDate),
                        "/worker/dashboard"
                );
            }
        } catch (Exception e) {
            log.warn("Could not send selection notification: {}", e.getMessage());
        }

        return saved;
    }

    @Transactional
    public AssignmentEntity confirmAssignment(UUID userId, UUID assignmentId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found for user: " + userId));

        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (!assignment.getWorkerId().equals(worker.getId())) {
            throw new SecurityException("Unauthorized: This assignment does not belong to you");
        }

        if (!"SELECTED".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Assignment cannot be confirmed in status: " + assignment.getStatus());
        }

        assignment.setStatus("CONFIRMED");
        assignment.setConfirmedAt(Instant.now());
        AssignmentEntity saved = assignmentRepository.save(assignment);

        // Update Job confirmed capacity
        JobEntity job = jobRepository.findById(assignment.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        int confirmedCount = (int) assignmentRepository.countByJobIdAndStatus(job.getId(), "CONFIRMED");
        job.setWorkersConfirmed(confirmedCount);

        if (job.getWorkersConfirmed() >= job.getWorkersRequired()) {
            job.setStatus("ASSIGNED");
        }
        jobRepository.save(job);

        return saved;
    }

    @Transactional
    public AssignmentEntity declineAssignment(UUID userId, UUID assignmentId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (!assignment.getWorkerId().equals(worker.getId())) {
            throw new SecurityException("Unauthorized");
        }

        assignment.setStatus("DECLINED");
        AssignmentEntity saved = assignmentRepository.save(assignment);

        // Decrement job confirmed count if necessary and reopen
        JobEntity job = jobRepository.findById(assignment.getJobId()).orElse(null);
        if (job != null) {
            int confirmedCount = (int) assignmentRepository.countByJobIdAndStatus(job.getId(), "CONFIRMED");
            job.setWorkersConfirmed(confirmedCount);
            if (confirmedCount < job.getWorkersRequired()) {
                job.setStatus("OPEN");
            }
            jobRepository.save(job);
        }

        return saved;
    }

    public List<AssignmentEntity> getWorkerAssignments(UUID userId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        return assignmentRepository.findByWorkerIdOrderByCreatedAtDesc(worker.getId());
    }

    public List<AssignmentEntity> getProviderAssignments(UUID userId) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return assignmentRepository.findByProviderIdOrderByCreatedAtDesc(provider.getId());
    }

    @Transactional
    public AssignmentEntity startWork(UUID userId, UUID assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
        assignment.setStatus("IN_PROGRESS");
        assignment.setWorkStartedAt(Instant.now());
        AssignmentEntity saved = assignmentRepository.save(assignment);

        JobEntity job = jobRepository.findById(assignment.getJobId()).orElse(null);
        if (job != null && !"IN_PROGRESS".equalsIgnoreCase(job.getStatus())) {
            job.setStatus("IN_PROGRESS");
            jobRepository.save(job);
        }

        // Notify Worker & Provider
        try {
            WorkerEntity worker = workerRepository.findById(assignment.getWorkerId()).orElse(null);
            if (worker != null) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "REMINDERS",
                        "🔔 काम सुरू झाले (Work Starts Today)",
                        String.format("आपले \"%s\" हे काम सुरू झाले आहे.", job != null ? job.getTitle() : "काम"),
                        "/worker/dashboard"
                );
            }
        } catch (Exception ignored) {}

        return saved;
    }

    @Transactional
    public AssignmentEntity requestCompletion(UUID userId, UUID assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
        assignment.setStatus("COMPLETION_REQUESTED");
        assignment.setCompletionRequestedAt(Instant.now());
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public AssignmentEntity confirmCompletion(UUID userId, UUID assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        JobEntity job = jobRepository.findById(assignment.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + assignment.getJobId()));

        assignment.setStatus("COMPLETED");
        assignment.setCompletedAt(Instant.now());
        assignment.setActualCompletionDate(LocalDate.now());

        BigDecimal basePayment = assignment.getAgreedWage() != null ? assignment.getAgreedWage() : job.getDailyWage();
        if (basePayment == null) basePayment = new BigDecimal("600.00");
        assignment.setBasePayment(basePayment);

        BigDecimal otAmount = assignment.getOvertimeAmount() != null ? assignment.getOvertimeAmount() : BigDecimal.ZERO;
        BigDecimal addAmount = assignment.getAdditionalAmount() != null ? assignment.getAdditionalAmount() : BigDecimal.ZERO;
        BigDecimal total = basePayment.add(otAmount).add(addAmount);
        assignment.setTotalAmount(total);
        assignment.setPaymentStatus("PENDING");

        AssignmentEntity saved = assignmentRepository.save(assignment);

        // Requirement #28 & #29: Create ONE persistent Completion Record for EACH WORK + SELECTED WORKER
        Optional<CompletionRecordEntity> existingRecord = completionRecordRepository.findByAssignmentId(assignment.getId());
        CompletionRecordEntity completionRecord;
        Instant now = Instant.now();
        Instant ratingAvailableTime = now.plus(1, ChronoUnit.DAYS); // Rating available next day

        if (existingRecord.isPresent()) {
            completionRecord = existingRecord.get();
            completionRecord.setCompletionStatus("COMPLETED");
            completionRecord.setBasePayment(basePayment);
            completionRecord.setOvertimeAmount(otAmount);
            completionRecord.setAdditionalAmount(addAmount);
            completionRecord.setTotalAmount(total);
            completionRecord.setPaymentStatus(assignment.getPaymentStatus());
            completionRecord.setCompletedAt(now);
            completionRecord.setRatingAvailableAt(ratingAvailableTime);
        } else {
            completionRecord = CompletionRecordEntity.builder()
                    .jobId(job.getId())
                    .assignmentId(assignment.getId())
                    .workerId(assignment.getWorkerId())
                    .providerId(assignment.getProviderId())
                    .workDate(assignment.getActualCompletionDate() != null ? assignment.getActualCompletionDate() : LocalDate.now())
                    .completionStatus("COMPLETED")
                    .basePayment(basePayment)
                    .overtimeAmount(otAmount)
                    .additionalAmount(addAmount)
                    .totalAmount(total)
                    .paymentStatus("PENDING")
                    .paymentType(assignment.getPaymentType() != null ? assignment.getPaymentType() : "CASH")
                    .workerRatingStatus("PENDING")
                    .providerRatingStatus("PENDING")
                    .completedAt(now)
                    .ratingAvailableAt(ratingAvailableTime)
                    .build();
        }
        completionRecordRepository.save(completionRecord);

        // Update Job Status if all assignments are completed
        List<AssignmentEntity> allJobAsgs = assignmentRepository.findByJobIdOrderByCreatedAtDesc(job.getId());
        boolean allCompleted = !allJobAsgs.isEmpty() && allJobAsgs.stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()) && !"DECLINED".equalsIgnoreCase(a.getStatus()))
                .allMatch(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()));

        if (allCompleted) {
            job.setStatus("COMPLETED");
            jobRepository.save(job);
        }

        // Notify Worker
        try {
            WorkerEntity worker = workerRepository.findById(assignment.getWorkerId()).orElse(null);
            if (worker != null) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "RATINGS_PAYMENTS",
                        "✅ काम पूर्ण झाले (Work Completed)",
                        String.format("आपले \"%s\" हे काम पूर्ण म्हणून नोंदवले गेले आहे. देय रक्कम: ₹%s", job.getTitle(), total.toPlainString()),
                        "/worker/dashboard"
                );
            }
        } catch (Exception ignored) {}

        return saved;
    }

    @Transactional
    public CompletionRecordEntity confirmPayment(UUID userId, UUID assignmentId, BigDecimal overtimeAmount, BigDecimal additionalAmount, String paymentType) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found for user: " + userId));

        if (!assignment.getProviderId().equals(provider.getId())) {
            throw new SecurityException("Unauthorized: You do not own this work assignment");
        }

        final BigDecimal base = assignment.getBasePayment() != null ? assignment.getBasePayment() : (assignment.getAgreedWage() != null ? assignment.getAgreedWage() : new BigDecimal("600.00"));
        final BigDecimal ot = overtimeAmount != null ? overtimeAmount : (assignment.getOvertimeAmount() != null ? assignment.getOvertimeAmount() : BigDecimal.ZERO);
        final BigDecimal add = additionalAmount != null ? additionalAmount : (assignment.getAdditionalAmount() != null ? assignment.getAdditionalAmount() : BigDecimal.ZERO);
        final BigDecimal total = base.add(ot).add(add);
        final String pType = paymentType != null ? paymentType.toUpperCase() : "CASH";

        assignment.setBasePayment(base);
        assignment.setOvertimeAmount(ot);
        assignment.setAdditionalAmount(add);
        assignment.setTotalAmount(total);
        assignment.setPaymentType(pType);
        assignment.setPaymentStatus("PAID");
        assignment.setPaymentConfirmedAt(Instant.now());
        assignmentRepository.save(assignment);

        CompletionRecordEntity record = completionRecordRepository.findByAssignmentId(assignment.getId())
                .orElseGet(() -> CompletionRecordEntity.builder()
                        .jobId(assignment.getJobId())
                        .assignmentId(assignment.getId())
                        .workerId(assignment.getWorkerId())
                        .providerId(assignment.getProviderId())
                        .workDate(assignment.getActualCompletionDate() != null ? assignment.getActualCompletionDate() : LocalDate.now())
                        .completionStatus("COMPLETED")
                        .basePayment(base)
                        .overtimeAmount(ot)
                        .additionalAmount(add)
                        .totalAmount(total)
                        .paymentStatus("PENDING")
                        .workerRatingStatus("PENDING")
                        .providerRatingStatus("PENDING")
                        .completedAt(Instant.now())
                        .ratingAvailableAt(Instant.now())
                        .build());

        record.setBasePayment(base);
        record.setOvertimeAmount(ot);
        record.setAdditionalAmount(add);
        record.setTotalAmount(total);
        record.setPaymentType(pType);
        record.setPaymentStatus("PAID");
        record.setPaymentConfirmedAt(Instant.now());
        CompletionRecordEntity savedRecord = completionRecordRepository.save(record);

        // Notify Worker
        try {
            WorkerEntity worker = workerRepository.findById(assignment.getWorkerId()).orElse(null);
            if (worker != null) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "RATINGS_PAYMENTS",
                        "💵 मजुरी जमा झाली (Payment Received)",
                        String.format("आपल्याला ₹%s मजुरी (%s) प्राप्त झाली आहे.", total.toPlainString(), pType),
                        "/worker/dashboard"
                );
            }
        } catch (Exception ignored) {}

        return savedRecord;
    }

    @Transactional
    public AssignmentEntity acknowledgePayment(UUID userId, UUID assignmentId, String paymentType) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        assignment.setPaymentType(paymentType != null ? paymentType.toUpperCase() : "CASH");
        assignment.setPaymentStatus("PAID");
        assignment.setPaymentConfirmedByWorker(true);
        assignment.setPaymentConfirmedAt(Instant.now());
        AssignmentEntity saved = assignmentRepository.save(assignment);

        completionRecordRepository.findByAssignmentId(assignment.getId()).ifPresent(rec -> {
            rec.setPaymentStatus("PAID");
            rec.setPaymentType(assignment.getPaymentType());
            rec.setPaymentConfirmedAt(Instant.now());
            completionRecordRepository.save(rec);
        });

        return saved;
    }

    @Transactional
    public AttendanceEntity recordAttendance(UUID userId, UUID assignmentId, LocalDate workDate, LocalTime startTime, LocalTime endTime, String status, String remarks) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        LocalDate date = workDate != null ? workDate : LocalDate.now();
        Optional<AttendanceEntity> existing = attendanceRepository.findByAssignmentIdAndWorkDate(assignmentId, date);

        AttendanceEntity attendance = existing.orElseGet(() -> AttendanceEntity.builder()
                .jobId(assignment.getJobId())
                .assignmentId(assignmentId)
                .workerId(assignment.getWorkerId())
                .providerId(assignment.getProviderId())
                .workDate(date)
                .build());

        attendance.setStartTime(startTime);
        attendance.setEndTime(endTime);
        attendance.setAttendanceStatus(status != null ? status.toUpperCase() : "PRESENT");
        attendance.setRemarks(remarks);

        return attendanceRepository.save(attendance);
    }

    public List<AttendanceEntity> getAttendancesForAssignment(UUID assignmentId) {
        return attendanceRepository.findByAssignmentIdOrderByWorkDateDesc(assignmentId);
    }

    public List<AttendanceEntity> getAttendancesForJob(UUID jobId) {
        return attendanceRepository.findByJobIdOrderByWorkDateDesc(jobId);
    }

    public List<CompletionRecordEntity> getWorkerCompletionRecords(UUID userId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        return completionRecordRepository.findByWorkerIdOrderByCreatedAtDesc(worker.getId());
    }

    public List<CompletionRecordEntity> getProviderCompletionRecords(UUID userId) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return completionRecordRepository.findByProviderIdOrderByCreatedAtDesc(provider.getId());
    }

    public List<CompletionRecordEntity> getJobCompletionRecords(UUID jobId) {
        return completionRecordRepository.findByJobId(jobId);
    }

    @Transactional
    public AssignmentEntity cancelAssignment(UUID userId, UUID assignmentId, String reason) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
        assignment.setStatus("CANCELLED");
        assignment.setCancelReason(reason);
        assignment.setCancelledAt(Instant.now());
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public AssignmentEntity reportNoShow(UUID userId, UUID assignmentId, String notes) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
        assignment.setStatus("NO_SHOW");
        assignment.setCancelReason("No-Show: " + notes);
        assignment.setCancelledAt(Instant.now());
        return assignmentRepository.save(assignment);
    }
}
