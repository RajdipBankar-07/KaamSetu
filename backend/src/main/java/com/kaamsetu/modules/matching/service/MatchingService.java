package com.kaamsetu.modules.matching.service;

import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.repository.JobRepository;
import com.kaamsetu.modules.matching.dto.JobMatchDto;
import com.kaamsetu.modules.matching.dto.WorkerMatchDto;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;

    /**
     * Haversine formula to compute great-circle distance between two geographic coordinates in kilometers.
     */
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 2.5; // Default fallback distance in local village radius
        }

        double earthRadius = 6371.0; // Radius of Earth in kilometers
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return Math.round(distance * 10.0) / 10.0;
    }

    /**
     * Get personalized matching jobs for authenticated Worker.
     */
    public List<JobMatchDto> getRecommendedJobsForWorker(UUID userId, Integer maxRadiusKm) {
        WorkerEntity worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found for user: " + userId));

        int effectiveRadius = (maxRadiusKm != null && maxRadiusKm > 0) ? maxRadiusKm : worker.getTravelRadiusKm();
        List<JobEntity> openJobs = jobRepository.findByStatusOrderByCreatedAtDesc("OPEN");
        List<JobMatchDto> matchedJobs = new ArrayList<>();

        for (JobEntity job : openJobs) {
            double distance = calculateDistance(worker.getLatitude(), worker.getLongitude(), job.getLatitude(), job.getLongitude());
            if (distance > effectiveRadius) {
                continue; // Outside worker's travel radius
            }

            boolean wageSatisfied = worker.getMinDailyWage() == null ||
                    job.getDailyWage().compareTo(worker.getMinDailyWage()) >= 0;

            boolean isUrgent = "URGENT".equalsIgnoreCase(job.getPriority());

            // Compute composite match percentage (0 - 100)
            int score = 30; // Base presence
            if (wageSatisfied) score += 20;
            if (distance <= 5.0) score += 15;
            else if (distance <= 10.0) score += 10;
            if (isUrgent) score += 10;

            // Hierarchical location affinity bonus
            if (worker.getVillageId() != null && job.getVillageId() != null && worker.getVillageId().equalsIgnoreCase(job.getVillageId())) {
                score += 25; // Same exact village
            } else if (worker.getVillage() != null && job.getVillage() != null && worker.getVillage().equalsIgnoreCase(job.getVillage())) {
                score += 25;
            } else if (worker.getTalukaId() != null && job.getTalukaId() != null && worker.getTalukaId().equalsIgnoreCase(job.getTalukaId())) {
                score += 15; // Same taluka
            } else if (worker.getTaluka() != null && job.getTaluka() != null && worker.getTaluka().equalsIgnoreCase(job.getTaluka())) {
                score += 15;
            } else if (worker.getDistrictId() != null && job.getDistrictId() != null && worker.getDistrictId().equalsIgnoreCase(job.getDistrictId())) {
                score += 5; // Same district
            }

            score = Math.min(score, 100);

            matchedJobs.add(JobMatchDto.builder()
                    .job(job)
                    .distanceKm(distance)
                    .matchPercentage(score)
                    .categoryMatched(true)
                    .withinRadius(true)
                    .wageSatisfied(wageSatisfied)
                    .isUrgent(isUrgent)
                    .build());
        }

        // Sort primarily by Urgent priority, then highest match percentage, then shortest distance
        matchedJobs.sort(Comparator
                .comparing((JobMatchDto j) -> j.isUrgent() ? 0 : 1)
                .thenComparing(JobMatchDto::getMatchPercentage, Comparator.reverseOrder())
                .thenComparing(JobMatchDto::getDistanceKm));

        return matchedJobs;
    }

    /**
     * Get recommended nearby available candidates for Provider.
     */
    public List<WorkerMatchDto> getRecommendedWorkersForJob(UUID userId, String category, Integer maxRadiusKm) {
        ProviderEntity provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found for user: " + userId));

        int radius = (maxRadiusKm != null && maxRadiusKm > 0) ? maxRadiusKm : 15;
        List<WorkerEntity> allWorkers = workerRepository.findAll();
        List<WorkerMatchDto> candidates = new ArrayList<>();

        for (WorkerEntity worker : allWorkers) {
            if (!Boolean.TRUE.equals(worker.isAvailableToday())) {
                continue;
            }

            double distance = calculateDistance(provider.getLatitude(), provider.getLongitude(), worker.getLatitude(), worker.getLongitude());
            if (distance > radius) {
                continue;
            }

            boolean highTrust = worker.getTrustIndex() != null && worker.getTrustIndex().compareTo(new BigDecimal("4.5")) >= 0;
            int score = 50;
            if (highTrust) score += 25;
            if (distance <= 5.0) score += 25;
            else if (distance <= 10.0) score += 15;

            score = Math.min(score, 100);

            candidates.add(WorkerMatchDto.builder()
                    .worker(worker)
                    .distanceKm(distance)
                    .matchPercentage(score)
                    .availableToday(true)
                    .skillMatched(true)
                    .highTrust(highTrust)
                    .build());
        }

        candidates.sort(Comparator.comparing(WorkerMatchDto::getMatchPercentage, Comparator.reverseOrder())
                .thenComparing(WorkerMatchDto::getDistanceKm));

        return candidates;
    }
}
