package com.kaamsetu.modules.review.repository;

import com.kaamsetu.modules.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findByRevieweeIdOrderByCreatedAtDesc(UUID revieweeId);

    List<ReviewEntity> findByReviewerId(UUID reviewerId);

    List<ReviewEntity> findByJobId(UUID jobId);

    List<ReviewEntity> findByAssignmentId(UUID assignmentId);

    boolean existsByJobIdAndReviewerIdAndRevieweeId(UUID jobId, UUID reviewerId, UUID revieweeId);

    boolean existsByAssignmentIdAndReviewerId(UUID assignmentId, UUID reviewerId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.revieweeId = :userId")
    BigDecimal calculateAverageRatingForUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.revieweeId = :userId")
    long countReviewsForUser(@Param("userId") UUID userId);
}
