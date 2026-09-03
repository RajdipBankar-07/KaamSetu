package com.kaamsetu.modules.provider.repository;

import com.kaamsetu.modules.provider.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, UUID> {

    Optional<ProviderEntity> findByUserId(UUID userId);
}
