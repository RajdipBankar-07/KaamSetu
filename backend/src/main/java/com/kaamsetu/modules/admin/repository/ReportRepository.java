package com.kaamsetu.modules.admin.repository;

import com.kaamsetu.modules.admin.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    List<ReportEntity> findByOrderByCreatedAtDesc();

    List<ReportEntity> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);
}
