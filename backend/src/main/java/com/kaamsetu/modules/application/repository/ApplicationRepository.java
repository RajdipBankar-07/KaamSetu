package com.kaamsetu.modules.application.repository;

import com.kaamsetu.modules.application.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

    List<ApplicationEntity> findByJobIdOrderByCreatedAtDesc(UUID jobId);

    List<ApplicationEntity> findByWorkerIdOrderByCreatedAtDesc(UUID workerId);

    Optional<ApplicationEntity> findByJobIdAndWorkerId(UUID jobId, UUID workerId);

    boolean existsByJobIdAndWorkerId(UUID jobId, UUID workerId);

    long countByJobIdIn(List<UUID> jobIds);

    long countByWorkerId(UUID workerId);
}
