package com.kaamsetu.modules.admin.repository;

import com.kaamsetu.modules.admin.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findTop50ByOrderByCreatedAtDesc();
}

