package com.kaamsetu.modules.application.service;

import com.kaamsetu.modules.application.dto.ApplyJobRequest;
import com.kaamsetu.modules.application.entity.ApplicationEntity;
import com.kaamsetu.modules.application.repository.ApplicationRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final NotificationService notificationService;

    @Transactional
    public ApplicationEntity applyToJob(UUID userId, UUID jobId, ApplyJobRequest request) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker profile not found for user: " + userId));

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        // 1. Deadline Check
        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            job.setStatus("EXPIRED");
            jobRepository.save(job);
            throw new IllegalStateException("Application deadline has passed for this job");
        }

        // 2. Capacity Check
        if (job.getWorkersConfirmed() >= job.getWorkersRequired() ||
            "FULL".equalsIgnoreCase(job.getStatus()) ||
            "FILLED".equalsIgnoreCase(job.getStatus()) ||
            "CLOSED".equalsIgnoreCase(job.getStatus())) {
            throw new IllegalStateException("Job is already full and not accepting new applications");
        }

        // 3. Status Check
        if (!"OPEN".equalsIgnoreCase(job.getStatus()) && !"APPLICATIONS".equalsIgnoreCase(job.getStatus())) {
            throw new IllegalStateException("Job is not open for applications (Status: " + job.getStatus() + ")");
        }

        // 4. Duplicate Check
        if (applicationRepository.existsByJobIdAndWorkerId(jobId, worker.getId())) {
            throw new IllegalStateException("Worker has already applied to this job");
        }

        ApplicationEntity application = ApplicationEntity.builder()
                .jobId(jobId)
                .workerId(worker.getId())
                .status("APPLIED")
                .appliedWage(request != null && request.getAppliedWage() != null ? request.getAppliedWage() : job.getDailyWage())
                .workerNotes(request != null ? request.getWorkerNotes() : null)
                .build();

        ApplicationEntity saved = applicationRepository.save(application);

        // Notify Provider
        try {
            ProviderEntity provider = providerRepository.findById(job.getProviderId()).orElse(null);
            if (provider != null) {
                notificationService.createNotification(
                        provider.getUserId(),
                        "APPLICATIONS",
                        "📝 नवीन अर्ज प्राप्त झाला (New Application Received)",
                        String.format("%s यांनी आपल्या \"%s\" कामासाठी अर्ज केला आहे.",
                                worker.getFullName() != null ? worker.getFullName() : "कामगार",
                                job.getTitle()),
                        "/provider/dashboard"
                );
            }
        } catch (Exception e) {
            log.warn("Could not dispatch application notification: {}", e.getMessage());
        }

        return saved;
    }

    public List<ApplicationEntity> getWorkerApplications(UUID userId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker profile not found for user: " + userId));
        return applicationRepository.findByWorkerIdOrderByCreatedAtDesc(worker.getId());
    }

    public List<ApplicationEntity> getJobApplications(UUID userId, UUID jobId) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found for user: " + userId));
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!job.getProviderId().equals(provider.getId())) {
            throw new SecurityException("Unauthorized: You do not own this job");
        }

        return applicationRepository.findByJobIdOrderByCreatedAtDesc(jobId);
    }

    @Transactional
    public ApplicationEntity updateApplicationStatus(UUID userId, UUID applicationId, String newStatus) {
        ApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
        JobEntity job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        if (!job.getProviderId().equals(provider.getId())) {
            throw new SecurityException("Unauthorized");
        }

        application.setStatus(newStatus.toUpperCase());
        return applicationRepository.save(application);
    }

    @Transactional
    public ApplicationEntity withdrawApplication(UUID userId, UUID applicationId) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker profile not found for user: " + userId));
        ApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));

        if (!application.getWorkerId().equals(worker.getId())) {
            throw new SecurityException("Unauthorized: You do not own this application");
        }

        application.setStatus("WITHDRAWN");
        return applicationRepository.save(application);
    }
}
