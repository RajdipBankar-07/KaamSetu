package com.kaamsetu.modules.admin.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity extends BaseEntity {

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(name = "target_type", nullable = false, length = 30)
    private String targetType; // 'WORKER' | 'PROVIDER' | 'JOB' | 'MESSAGE' | 'PROFILE'

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "reason_code", nullable = false, length = 50)
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING"; // 'PENDING' | 'INVESTIGATING' | 'RESOLVED' | 'DISMISSED'

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
