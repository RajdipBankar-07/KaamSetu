package com.kaamsetu.modules.application.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEntity extends BaseEntity {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "worker_id", nullable = false)
    private UUID workerId;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "APPLIED"; // 'APPLIED' | 'SHORTLISTED' | 'REJECTED' | 'WITHDRAWN'

    @Column(name = "applied_wage", precision = 10, scale = 2)
    private BigDecimal appliedWage;

    @Column(name = "worker_notes", columnDefinition = "TEXT")
    private String workerNotes;
}
