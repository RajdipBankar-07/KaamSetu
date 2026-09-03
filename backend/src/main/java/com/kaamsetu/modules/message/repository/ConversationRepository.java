package com.kaamsetu.modules.message.repository;

import com.kaamsetu.modules.message.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {

    Optional<ConversationEntity> findByUserId(UUID userId);

    Optional<ConversationEntity> findByAdminUserIdAndUserId(UUID adminUserId, UUID userId);

    List<ConversationEntity> findAllByOrderByUpdatedAtDesc();
}
