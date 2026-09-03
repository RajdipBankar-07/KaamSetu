package com.kaamsetu.modules.assignment.repository;

import com.kaamsetu.modules.assignment.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findByJobIdOrderByWorkDateDesc(UUID jobId);

    List<AttendanceEntity> findByAssignmentIdOrderByWorkDateDesc(UUID assignmentId);

    List<AttendanceEntity> findByWorkerIdOrderByWorkDateDesc(UUID workerId);

    Optional<AttendanceEntity> findByAssignmentIdAndWorkDate(UUID assignmentId, LocalDate workDate);
}
