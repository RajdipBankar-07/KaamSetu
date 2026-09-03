package com.kaamsetu.modules.assignment.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentEntity extends BaseEntity {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "application_id")
    private UUID applicationId;

    @Column(name = "worker_id", nullable = false)
    private UUID workerId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "SELECTED"; // 'APPLIED' | 'SELECTED' | 'CONFIRMED' | 'DECLINED' | 'NO_RESPONSE' | 'IN_PROGRESS' | 'COMPLETION_REQUESTED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW' | 'DISPUTED'

    @Column(name = "agreed_wage", nullable = false, precision = 10, scale = 2)
    private BigDecimal agreedWage;

    @Column(name = "start_date")
    private java.time.LocalDate startDate;

    @Column(name = "expected_completion_date")
    private java.time.LocalDate expectedCompletionDate;

    @Column(name = "actual_completion_date")
    private java.time.LocalDate actualCompletionDate;

    @Column(name = "selection_window_expires_at")
    private Instant selectionWindowExpiresAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "work_started_at")
    private Instant workStartedAt;

    @Column(name = "completion_requested_at")
    private Instant completionRequestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "payment_type", nullable = false, length = 20)
    @Builder.Default
    private String paymentType = "CASH"; // 'CASH' | 'UPI' | 'DIRECT_BANK'

    @Column(name = "base_payment", precision = 10, scale = 2)
    private BigDecimal basePayment;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "overtime_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal overtimeAmount = BigDecimal.ZERO;

    @Column(name = "additional_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal additionalAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING"; // 'PENDING' | 'PAID' | 'PAID_CONFIRMED' | 'DISPUTED'

    @Column(name = "payment_confirmed_by_worker", nullable = false)
    @Builder.Default
    private Boolean paymentConfirmedByWorker = false;

    @Column(name = "payment_confirmed_at")
    private Instant paymentConfirmedAt;

    @Column(name = "cancelled_by", length = 30)
    private String cancelledBy;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;
}
