package com.kaamsetu.modules.assignment.service;

import com.kaamsetu.modules.assignment.entity.CompletionRecordEntity;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LifecycleSchedulerService {

    private final CompletionRecordRepository completionRecordRepository;
    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;
    private final WorkerRepository workerRepository;
    private final NotificationService notificationService;

    /**
     * Daily check for pending payments and pending bilateral ratings.
     * Can also be triggered manually or via tests.
     */
    @Transactional
    public void processNextDayReminders() {
        log.info("Running Next-Day Payment & Rating Reminder processor...");
        LocalDate today = LocalDate.now();

        // 1. Pending Payment Reminders -> Notify Provider
        List<CompletionRecordEntity> pendingPayments = completionRecordRepository.findByPaymentStatus("PENDING");
        for (CompletionRecordEntity record : pendingPayments) {
            if (record.getWorkDate() != null && record.getWorkDate().isBefore(today)) {
                ProviderEntity provider = providerRepository.findById(record.getProviderId()).orElse(null);
                JobEntity job = jobRepository.findById(record.getJobId()).orElse(null);
                if (provider != null) {
                    notificationService.createNotification(
                            provider.getUserId(),
                            "REMINDERS",
                            "🔔 मजुरी प्रलंबित (Payment Pending)",
                            String.format("कृपया \"%s\" कामासाठी कामगाराची मजुरी पुष्टीकरण पूर्ण करा.",
                                    job != null ? job.getTitle() : "काम"),
                            "/provider/dashboard"
                    );
                }
            }
        }

        // 2. Pending Worker Ratings -> Notify Worker
        List<CompletionRecordEntity> pendingWorkerRatings = completionRecordRepository.findByWorkerRatingStatus("PENDING");
        for (CompletionRecordEntity record : pendingWorkerRatings) {
            WorkerEntity worker = workerRepository.findById(record.getWorkerId()).orElse(null);
            JobEntity job = jobRepository.findById(record.getJobId()).orElse(null);
            if (worker != null) {
                notificationService.createNotification(
                        worker.getUserId(),
                        "RATINGS_PAYMENTS",
                        "⭐ अभिप्राय प्रलंबित (Rating Pending)",
                        String.format("आपण \"%s\" कामासाठी नियोक्त्याला रेटिंग देऊ शकता.",
                                job != null ? job.getTitle() : "काम"),
                        "/worker/dashboard"
                );
            }
        }

        // 3. Pending Provider Ratings -> Notify Provider
        List<CompletionRecordEntity> pendingProviderRatings = completionRecordRepository.findByProviderRatingStatus("PENDING");
        for (CompletionRecordEntity record : pendingProviderRatings) {
            ProviderEntity provider = providerRepository.findById(record.getProviderId()).orElse(null);
            JobEntity job = jobRepository.findById(record.getJobId()).orElse(null);
            if (provider != null) {
                notificationService.createNotification(
                        provider.getUserId(),
                        "RATINGS_PAYMENTS",
                        "⭐ कामगार अभिप्राय (Rate Worker)",
                        String.format("कृपया \"%s\" कामासाठी कामगाराला रेटिंग व अभिप्राय द्या.",
                                job != null ? job.getTitle() : "काम"),
                        "/provider/dashboard"
                );
            }
        }
    }
}
