package com.kaamsetu.modules.worker.repository;

import com.kaamsetu.modules.worker.entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {

    Optional<WorkerEntity> findByUserId(UUID userId);

    List<WorkerEntity> findByVillage(String village);

    List<WorkerEntity> findByTaluka(String taluka);
}
