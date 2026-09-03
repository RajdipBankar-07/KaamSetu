package com.kaamsetu.modules.review.service;

import com.kaamsetu.modules.assignment.entity.AssignmentEntity;
import com.kaamsetu.modules.assignment.entity.CompletionRecordEntity;
import com.kaamsetu.modules.assignment.repository.AssignmentRepository;
import com.kaamsetu.modules.assignment.repository.CompletionRecordRepository;
import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.repository.JobRepository;
import com.kaamsetu.modules.notification.service.NotificationService;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.review.dto.PendingRatingDto;
import com.kaamsetu.modules.review.dto.SubmitReviewRequest;
import com.kaamsetu.modules.review.entity.ReviewEntity;
import com.kaamsetu.modules.review.repository.ReviewRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.repository.UserRepository;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Kolkata");

    private final ReviewRepository reviewRepository;
    private final AssignmentRepository assignmentRepository;
    private final CompletionRecordRepository completionRecordRepository;
    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Rating Eligibility Rule:
     * Rating is permitted for COMPLETED assignments.
     */
    public boolean isNextDayRatingEligible(AssignmentEntity assignment) {
        if (assignment == null || !"COMPLETED".equalsIgnoreCase(assignment.getStatus())) {
            return false;
        }
        return true;
    }

    /**
     * Fetch all completed assignments for the authenticated user that are eligible for rating
     * and have not yet been rated by this user.
     */
    public List<PendingRatingDto> getPendingRatings(UUID userId) {
        List<PendingRatingDto> pending = new ArrayList<>();

        // 1. Check if user is a Worker
        Optional<WorkerEntity> workerOpt = workerRepository.findByUserId(userId);
        if (workerOpt.isPresent()) {
            WorkerEntity worker = workerOpt.get();
            List<AssignmentEntity> completed = assignmentRepository.findByWorkerIdAndStatus(worker.getId(), "COMPLETED");
            for (AssignmentEntity asg : completed) {
                if (isNextDayRatingEligible(asg) && !reviewRepository.existsByAssignmentIdAndReviewerId(asg.getId(), userId)) {
                    JobEntity job = jobRepository.findById(asg.getJobId()).orElse(null);
                    ProviderEntity provider = providerRepository.findById(asg.getProviderId()).orElse(null);
                    UserEntity providerUser = provider != null ? userRepository.findById(provider.getUserId()).orElse(null) : null;
                    String pAvatar = (providerUser != null && providerUser.getGender() == com.kaamsetu.modules.user.entity.enums.GenderEnum.FEMALE) ? "👩" : "👨";
                    pending.add(PendingRatingDto.builder()
                            .assignmentId(asg.getId())
                            .jobId(asg.getJobId())
                            .jobTitle(job != null ? job.getTitle() : "शेतातील काम")
                            .category(job != null ? job.getCategory() : "cat.agriculture")
                            .otherPartyUserId(provider != null ? provider.getUserId() : null)
                            .otherPartyName(providerUser != null ? (providerUser.getFullName() != null ? providerUser.getFullName() : providerUser.getUsername()) : "नियोक्ता (Provider)")
                            .otherPartyRole("PROVIDER")
                            .otherPartyAvatar(pAvatar)
                            .startDate(asg.getStartDate() != null ? asg.getStartDate() : (job != null ? job.getStartDate() : null))
                            .expectedCompletionDate(asg.getExpectedCompletionDate())
                            .actualCompletionDate(asg.getActualCompletionDate())
                            .completedAt(asg.getCompletedAt())
                            .agreedWage(asg.getAgreedWage())
                            .isEligibleForRating(true)
                            .build());
                }
            }
        }

        // 2. Check if user is a Provider
        Optional<ProviderEntity> providerOpt = providerRepository.findByUserId(userId);
        if (providerOpt.isPresent()) {
            ProviderEntity provider = providerOpt.get();
            List<AssignmentEntity> completed = assignmentRepository.findByProviderIdAndStatus(provider.getId(), "COMPLETED");
            for (AssignmentEntity asg : completed) {
                if (isNextDayRatingEligible(asg) && !reviewRepository.existsByAssignmentIdAndReviewerId(asg.getId(), userId)) {
                    JobEntity job = jobRepository.findById(asg.getJobId()).orElse(null);
                    WorkerEntity worker = workerRepository.findById(asg.getWorkerId()).orElse(null);
                    UserEntity workerUser = worker != null ? userRepository.findById(worker.getUserId()).orElse(null) : null;

                    String wAvatar = (workerUser != null && workerUser.getGender() == com.kaamsetu.modules.user.entity.enums.GenderEnum.FEMALE) ? "👷‍♀️" : "👷‍♂️";
                    pending.add(PendingRatingDto.builder()
                            .assignmentId(asg.getId())
                            .jobId(asg.getJobId())
                            .jobTitle(job != null ? job.getTitle() : "शेतातील काम")
                            .category(job != null ? job.getCategory() : "cat.agriculture")
                            .otherPartyUserId(worker != null ? worker.getUserId() : null)
                            .otherPartyName(workerUser != null ? (workerUser.getFullName() != null ? workerUser.getFullName() : workerUser.getUsername()) : "कामगार (Worker)")
                            .otherPartyRole("WORKER")
                            .otherPartyAvatar(wAvatar)
                            .startDate(asg.getStartDate() != null ? asg.getStartDate() : (job != null ? job.getStartDate() : null))
                            .expectedCompletionDate(asg.getExpectedCompletionDate())
                            .actualCompletionDate(asg.getActualCompletionDate())
                            .completedAt(asg.getCompletedAt())
                            .agreedWage(asg.getAgreedWage())
                            .isEligibleForRating(true)
                            .build());
                }
            }
        }

        return pending;
    }

    /**
     * Submit Two-Way Multi-Dimensional Review & Rating
     */
    @Transactional
    public ReviewEntity submitReview(UUID reviewerUserId, SubmitReviewRequest request) {
        // 1. Resolve Assignment
        AssignmentEntity assignment = null;
        if (request.getAssignmentId() != null) {
            assignment = assignmentRepository.findById(request.getAssignmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + request.getAssignmentId()));
        } else if (request.getJobId() != null && request.getRevieweeId() != null) {
            List<AssignmentEntity> asgs = assignmentRepository.findByJobIdOrderByCreatedAtDesc(request.getJobId());
            for (AssignmentEntity a : asgs) {
                Optional<WorkerEntity> w = workerRepository.findById(a.getWorkerId());
                Optional<ProviderEntity> p = providerRepository.findById(a.getProviderId());
                if ((w.isPresent() && w.get().getUserId().equals(request.getRevieweeId())) ||
                    (p.isPresent() && p.get().getUserId().equals(request.getRevieweeId()))) {
                    assignment = a;
                    break;
                }
            }
        }

        if (assignment == null) {
            throw new IllegalArgumentException("Valid assignment could not be identified for this review");
        }

        // 2. Validate Assignment is Completed
        if (!"COMPLETED".equalsIgnoreCase(assignment.getStatus())) {
            throw new IllegalStateException("Ratings are only permitted for COMPLETED assignments");
        }

        // 3. Determine Reviewer Role & Reviewee Identity Server-Side
        Optional<WorkerEntity> workerOpt = workerRepository.findById(assignment.getWorkerId());
        Optional<ProviderEntity> providerOpt = providerRepository.findById(assignment.getProviderId());

        if (workerOpt.isEmpty() || providerOpt.isEmpty()) {
            throw new IllegalStateException("Invalid assignment participants in database");
        }

        WorkerEntity worker = workerOpt.get();
        ProviderEntity provider = providerOpt.get();

        String reviewerRole;
        UUID revieweeUserId;

        if (worker.getUserId().equals(reviewerUserId)) {
            reviewerRole = "WORKER";
            revieweeUserId = provider.getUserId();
        } else if (provider.getUserId().equals(reviewerUserId)) {
            reviewerRole = "PROVIDER";
            revieweeUserId = worker.getUserId();
        } else {
            throw new SecurityException("Unauthorized: You did not participate in this assignment");
        }

        // 4. Validate Eligibility
        if (!isNextDayRatingEligible(assignment)) {
            throw new IllegalStateException("Ratings are only permitted once work has been completed");
        }

        // 5. Prevent Duplicate Rating
        if (reviewRepository.existsByAssignmentIdAndReviewerId(assignment.getId(), reviewerUserId)) {
            throw new IllegalStateException("You have already submitted a rating for this assignment");
        }

        // 6. Save Review Entity with Multi-Dimensional Categories
        BigDecimal defaultCategoryRating = request.getRating();
        ReviewEntity review = ReviewEntity.builder()
                .assignmentId(assignment.getId())
                .jobId(assignment.getJobId())
                .providerId(provider.getId())
                .workerId(worker.getId())
                .reviewerId(reviewerUserId)
                .revieweeId(revieweeUserId)
                .reviewerRole(reviewerRole)
                .rating(request.getRating())
                .punctualityRating(request.getPunctualityRating() != null ? request.getPunctualityRating() : defaultCategoryRating)
                .qualityRating(request.getQualityRating() != null ? request.getQualityRating() : defaultCategoryRating)
                .behaviorRating(request.getBehaviorRating() != null ? request.getBehaviorRating() : defaultCategoryRating)
                .workManagementRating(request.getWorkManagementRating() != null ? request.getWorkManagementRating() : defaultCategoryRating)
                .paymentExperienceRating(request.getPaymentExperienceRating() != null ? request.getPaymentExperienceRating() : defaultCategoryRating)
                .timeManagementRating(request.getTimeManagementRating() != null ? request.getTimeManagementRating() : defaultCategoryRating)
                .reliabilityRating(request.getReliabilityRating() != null ? request.getReliabilityRating() : defaultCategoryRating)
                .skillRating(request.getSkillRating() != null ? request.getSkillRating() : defaultCategoryRating)
                .overallExperienceRating(request.getOverallExperienceRating() != null ? request.getOverallExperienceRating() : defaultCategoryRating)
                .reviewText(request.getReviewText())
                .build();

        ReviewEntity saved = reviewRepository.save(review);

        // 7. Update Completion Record rating tracking
        completionRecordRepository.findByAssignmentId(assignment.getId()).ifPresent(record -> {
            if ("WORKER".equalsIgnoreCase(reviewerRole)) {
                record.setProviderRatingStatus("COMPLETED");
                record.setProviderRatingId(saved.getId());
            } else {
                record.setWorkerRatingStatus("COMPLETED");
                record.setWorkerRatingId(saved.getId());
            }
            completionRecordRepository.save(record);
        });

        // 8. Dynamically Recalculate Average Rating and Trust Index on User Profiles
        updateUserRatingAndTrust(revieweeUserId);

        // 9. Trigger Notification
        try {
            notificationService.createNotification(
                    revieweeUserId,
                    "RATINGS_PAYMENTS",
                    "⭐ नवीन रेटिंग व अभिप्राय (New Rating Received)",
                    String.format("आपल्याला %s⭐ रेटिंग मिळाले आहे: \"%s\"",
                            request.getRating().toString(),
                            request.getReviewText() != null ? request.getReviewText() : "उत्कृष्ट सहकार्य"),
                    "/profile"
            );
        } catch (Exception e) {
            log.warn("Could not dispatch rating notification: {}", e.getMessage());
        }

        return saved;
    }

    public List<ReviewEntity> getUserReviews(UUID userId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId);
    }

    public BigDecimal getAverageRating(UUID userId) {
        return reviewRepository.calculateAverageRatingForUser(userId);
    }

    public long getRatingCount(UUID userId) {
        return reviewRepository.countReviewsForUser(userId);
    }

    private void updateUserRatingAndTrust(UUID userId) {
        BigDecimal avg = reviewRepository.calculateAverageRatingForUser(userId);
        if (avg == null) return;

        BigDecimal roundedAvg = avg.setScale(1, RoundingMode.HALF_UP);

        // Update Worker if reviewee is worker
        Optional<WorkerEntity> workerOpt = workerRepository.findByUserId(userId);
        if (workerOpt.isPresent()) {
            WorkerEntity worker = workerOpt.get();
            worker.setRatingAvg(roundedAvg);
            BigDecimal trust = roundedAvg.multiply(new BigDecimal("0.9")).add(new BigDecimal("0.5")).min(new BigDecimal("5.0"));
            worker.setTrustIndex(trust.setScale(1, RoundingMode.HALF_UP));
            workerRepository.save(worker);
            return;
        }

        // Update Provider if reviewee is provider
        Optional<ProviderEntity> providerOpt = providerRepository.findByUserId(userId);
        if (providerOpt.isPresent()) {
            ProviderEntity provider = providerOpt.get();
            provider.setRatingAvg(roundedAvg);
            BigDecimal trust = roundedAvg.multiply(new BigDecimal("0.9")).add(new BigDecimal("0.5")).min(new BigDecimal("5.0"));
            provider.setTrustIndex(trust.setScale(1, RoundingMode.HALF_UP));
            providerRepository.save(provider);
        }
    }
}
