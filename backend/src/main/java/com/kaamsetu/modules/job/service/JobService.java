package com.kaamsetu.modules.job.service;

import com.kaamsetu.modules.job.dto.CreateJobRequest;
import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.repository.JobRepository;
import com.kaamsetu.modules.location.service.LocationHierarchyService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;
    private final WorkerRepository workerRepository;
    private final NotificationService notificationService;
    private final LocationHierarchyService locationHierarchyService;

    @Transactional
    public JobEntity createJob(UUID userId, CreateJobRequest request) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found for user: " + userId));

        // Date and parameter business rule validations
        LocalDate today = LocalDate.now();
        if (request.getStartDate() != null && request.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Start date cannot be in the past: " + request.getStartDate());
        }

        if (request.getDeadline() != null) {
            if (request.getDeadline().isBefore(today)) {
                throw new IllegalArgumentException("Deadline cannot be in the past: " + request.getDeadline());
            }
            if (request.getStartDate() != null && request.getDeadline().isAfter(request.getStartDate())) {
                throw new IllegalArgumentException("Deadline cannot be after work start date: " + request.getDeadline());
            }
        }

        if (request.getWorkersRequired() != null && request.getWorkersRequired() < 1) {
            throw new IllegalArgumentException("Workers required must be at least 1: " + request.getWorkersRequired());
        }

        if (request.getDailyWage() == null || request.getDailyWage().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Daily wage must be positive: " + request.getDailyWage());
        }

        if (locationHierarchyService != null) {
            locationHierarchyService.validateHierarchy(
                    request.getCountryId(),
                    request.getStateId(),
                    request.getDistrictId(),
                    request.getTalukaId(),
                    request.getVillageId()
            );
        }

        JobEntity job = JobEntity.builder()
                .providerId(provider.getId())
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .countryId(request.getCountryId() != null ? request.getCountryId() : "IN")
                .stateId(request.getStateId() != null ? request.getStateId() : "state-mh")
                .districtId(request.getDistrictId() != null ? request.getDistrictId() : "dist-pune")
                .talukaId(request.getTalukaId() != null ? request.getTalukaId() : "tal-shirur")
                .villageId(request.getVillageId())
                .village(request.getVillage())
                .taluka(request.getTaluka())
                .district(request.getDistrict())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .dailyWage(request.getDailyWage())
                .workersRequired(request.getWorkersRequired() != null && request.getWorkersRequired() > 0 ? request.getWorkersRequired() : 1)
                .workersConfirmed(0)
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .status("OPEN")
                .workModel(request.getWorkModel() != null ? request.getWorkModel() : "ONETIME")
                .deadline(request.getDeadline())
                .paymentUnit(request.getPaymentUnit() != null ? request.getPaymentUnit() : "PER_DAY")
                .overtimeAvailable(Boolean.TRUE.equals(request.getOvertimeAvailable()))
                .overtimeRate(request.getOvertimeRate())
                .additionalPaymentConditions(request.getAdditionalPaymentConditions())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .workingHours(request.getWorkingHours() != null ? request.getWorkingHours() : new java.math.BigDecimal("8.00"))
                .lunchBreak(request.getLunchBreak())
                .teaBreak(request.getTeaBreak())
                .otherBreak(request.getOtherBreak())
                .facilities(request.getFacilities())
                .facilityDetails(request.getFacilityDetails())
                .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                .recurrenceSchedule(request.getRecurrenceSchedule())
                .startDate(request.getStartDate())
                .durationDays(request.getDurationDays() != null ? request.getDurationDays() : 1)
                .build();

        JobEntity saved = jobRepository.save(job);

        // Targeted Worker Notifications upon OPEN job creation
        try {
            notifyTargetedWorkers(saved);
        } catch (Exception e) {
            log.warn("Targeted worker notification failed for job {}: {}", saved.getId(), e.getMessage());
        }

        return saved;
    }

    private void notifyTargetedWorkers(JobEntity job) {
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) return;

        List<WorkerEntity> workers = workerRepository.findAll();
        for (WorkerEntity worker : workers) {
            boolean skillMatch = worker.getSkills() != null && (
                    worker.getSkills().toLowerCase().contains(job.getCategory().toLowerCase()) ||
                    job.getCategory().toLowerCase().contains(worker.getSkills().toLowerCase())
            );

            boolean locationMatch = (job.getVillage() != null && job.getVillage().equalsIgnoreCase(worker.getVillage())) ||
                                    (job.getTaluka() != null && job.getTaluka().equalsIgnoreCase(worker.getTaluka()));

            if (skillMatch || locationMatch) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "JOBS",
                        "📢 नवीन काम उपलब्ध (New Relevant Job)",
                        String.format("नवीन काम उपलब्ध: %s (%s, %s). रोजंदारी: ₹%s",
                                job.getTitle(),
                                job.getVillage() != null ? job.getVillage() : "स्थानिक",
                                job.getTaluka() != null ? job.getTaluka() : "",
                                job.getDailyWage().toPlainString()),
                        "/marketplace"
                );
            }
        }
    }

    public List<JobEntity> getOpenJobs(String category, String taluka) {
        List<JobEntity> jobs = jobRepository.searchOpenJobs(category, taluka);
        return jobs.stream().map(this::checkAndExpireJob).filter(j -> "OPEN".equalsIgnoreCase(j.getStatus()) || "APPLICATIONS".equalsIgnoreCase(j.getStatus())).collect(Collectors.toList());
    }

    public List<JobEntity> getProviderJobs(UUID userId) {
        return getProviderJobs(userId, null);
    }

    public List<JobEntity> getProviderJobs(UUID userId, String status) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found for user: " + userId));

        List<JobEntity> jobs;
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            jobs = jobRepository.findByProviderIdAndStatusOrderByCreatedAtDesc(provider.getId(), status.trim().toUpperCase());
        } else {
            jobs = jobRepository.findByProviderIdOrderByCreatedAtDesc(provider.getId());
        }
        return jobs.stream().map(this::checkAndExpireJob).collect(Collectors.toList());
    }

    public List<JobEntity> getAllJobs() {
        return jobRepository.findAllByOrderByCreatedAtDesc().stream().map(this::checkAndExpireJob).collect(Collectors.toList());
    }

    public JobEntity getJobById(UUID jobId) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        return checkAndExpireJob(job);
    }

    @Transactional
    public JobEntity checkAndExpireJob(JobEntity job) {
        if (job == null) return null;
        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            if ("OPEN".equalsIgnoreCase(job.getStatus()) || "APPLICATIONS".equalsIgnoreCase(job.getStatus())) {
                job.setStatus("EXPIRED");
                return jobRepository.save(job);
            }
        }
        return job;
    }

    @Transactional
    public JobEntity updateJobStatus(UUID userId, UUID jobId, String newStatus) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found for user: " + userId));
        JobEntity job = getJobById(jobId);

        if (!job.getProviderId().equals(provider.getId())) {
            throw new SecurityException("Unauthorized: You do not own this job");
        }

        job.setStatus(newStatus.toUpperCase());
        return jobRepository.save(job);
    }
}
