package com.kaamsetu.modules.assignment.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "completion_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRecordEntity extends BaseEntity {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "worker_id", nullable = false)
    private UUID workerId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "completion_status", nullable = false, length = 30)
    @Builder.Default
    private String completionStatus = "COMPLETED"; // 'COMPLETED' | 'DISPUTED' | 'PENDING_CONFIRMATION'

    @Column(name = "base_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePayment;

    @Column(name = "overtime_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal overtimeAmount = BigDecimal.ZERO;

    @Column(name = "additional_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal additionalAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING"; // 'PENDING' | 'PAID' | 'DISPUTED'

    @Column(name = "payment_type", length = 20)
    @Builder.Default
    private String paymentType = "CASH"; // 'CASH' | 'UPI' | 'DIRECT_BANK'

    @Column(name = "worker_rating_status", nullable = false, length = 20)
    @Builder.Default
    private String workerRatingStatus = "PENDING"; // 'PENDING' | 'COMPLETED' | 'NOT_APPLICABLE'

    @Column(name = "provider_rating_status", nullable = false, length = 20)
    @Builder.Default
    private String providerRatingStatus = "PENDING"; // 'PENDING' | 'COMPLETED' | 'NOT_APPLICABLE'

    @Column(name = "worker_rating_id")
    private UUID workerRatingId;

    @Column(name = "provider_rating_id")
    private UUID providerRatingId;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "rating_available_at")
    private Instant ratingAvailableAt;

    @Column(name = "payment_confirmed_at")
    private Instant paymentConfirmedAt;
}
