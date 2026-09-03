package com.kaamsetu.modules.review.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignment_id", "reviewer_id"}, name = "uk_review_assignment_reviewer")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @Column(name = "assignment_id")
    private UUID assignmentId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "provider_id")
    private UUID providerId;

    @Column(name = "worker_id")
    private UUID workerId;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private UUID revieweeId;

    @Column(name = "reviewer_role", length = 20)
    private String reviewerRole; // 'PROVIDER' | 'WORKER'

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating; // 1.0 - 5.0

    @Column(name = "punctuality_rating", precision = 2, scale = 1)
    private BigDecimal punctualityRating;

    @Column(name = "quality_rating", precision = 2, scale = 1)
    private BigDecimal qualityRating;

    @Column(name = "behavior_rating", precision = 2, scale = 1)
    private BigDecimal behaviorRating;

    @Column(name = "work_management_rating", precision = 2, scale = 1)
    private BigDecimal workManagementRating;

    @Column(name = "payment_experience_rating", precision = 2, scale = 1)
    private BigDecimal paymentExperienceRating;

    @Column(name = "time_management_rating", precision = 2, scale = 1)
    private BigDecimal timeManagementRating;

    @Column(name = "reliability_rating", precision = 2, scale = 1)
    private BigDecimal reliabilityRating;

    @Column(name = "skill_rating", precision = 2, scale = 1)
    private BigDecimal skillRating;

    @Column(name = "overall_experience_rating", precision = 2, scale = 1)
    private BigDecimal overallExperienceRating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;
}
