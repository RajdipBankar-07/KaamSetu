package com.kaamsetu.modules.assignment.repository;

import com.kaamsetu.modules.assignment.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, UUID> {

    List<AssignmentEntity> findByWorkerIdOrderByCreatedAtDesc(UUID workerId);

    List<AssignmentEntity> findByProviderIdOrderByCreatedAtDesc(UUID providerId);

    List<AssignmentEntity> findByJobIdOrderByCreatedAtDesc(UUID jobId);

    List<AssignmentEntity> findByWorkerIdAndStatus(UUID workerId, String status);

    List<AssignmentEntity> findByProviderIdAndStatus(UUID providerId, String status);

    Optional<AssignmentEntity> findByJobIdAndWorkerId(UUID jobId, UUID workerId);

    long countByStatus(String status);

    long countByJobIdAndStatus(UUID jobId, String status);

    long countByProviderIdAndStatus(UUID providerId, String status);

    long countByWorkerIdAndStatus(UUID workerId, String status);

    long countByJobIdInAndStatus(List<UUID> jobIds, String status);
}
