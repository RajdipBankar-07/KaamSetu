package com.kaamsetu.modules.job.repository;

import com.kaamsetu.modules.job.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    List<JobEntity> findByProviderIdOrderByCreatedAtDesc(UUID providerId);

    List<JobEntity> findByProviderIdAndStatusOrderByCreatedAtDesc(UUID providerId, String status);

    List<JobEntity> findByStatusOrderByCreatedAtDesc(String status);

    List<JobEntity> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    long countByProviderId(UUID providerId);

    @Query("SELECT j FROM JobEntity j WHERE (j.status = 'OPEN' OR j.status = 'APPLICATIONS') " +
           "AND (:category IS NULL OR j.category = :category) " +
           "AND (:taluka IS NULL OR j.taluka = :taluka) " +
           "ORDER BY CASE WHEN j.priority = 'URGENT' THEN 0 ELSE 1 END, j.createdAt DESC")
    List<JobEntity> searchOpenJobs(@Param("category") String category, @Param("taluka") String taluka);
}
