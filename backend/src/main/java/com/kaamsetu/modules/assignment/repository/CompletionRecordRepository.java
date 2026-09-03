package com.kaamsetu.modules.assignment.repository;

import com.kaamsetu.modules.assignment.entity.CompletionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompletionRecordRepository extends JpaRepository<CompletionRecordEntity, UUID> {

    List<CompletionRecordEntity> findByJobId(UUID jobId);

    List<CompletionRecordEntity> findByWorkerIdOrderByCreatedAtDesc(UUID workerId);

    List<CompletionRecordEntity> findByProviderIdOrderByCreatedAtDesc(UUID providerId);

    Optional<CompletionRecordEntity> findByAssignmentId(UUID assignmentId);

    List<CompletionRecordEntity> findByPaymentStatus(String paymentStatus);

    List<CompletionRecordEntity> findByWorkerRatingStatus(String ratingStatus);

    List<CompletionRecordEntity> findByProviderRatingStatus(String ratingStatus);
}
