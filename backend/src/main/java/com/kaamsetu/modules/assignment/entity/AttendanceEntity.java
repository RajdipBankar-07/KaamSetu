package com.kaamsetu.modules.assignment.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEntity extends BaseEntity {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "worker_id", nullable = false)
    private UUID workerId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "attendance_status", nullable = false, length = 20)
    @Builder.Default
    private String attendanceStatus = "PRESENT"; // 'PRESENT' | 'ABSENT' | 'LATE' | 'HALF_DAY'

    @Column(name = "remarks", length = 300)
    private String remarks;
}
