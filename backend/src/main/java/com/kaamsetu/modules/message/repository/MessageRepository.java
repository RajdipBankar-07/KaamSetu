package com.kaamsetu.modules.message.repository;

import com.kaamsetu.modules.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    List<MessageEntity> findBySenderIdOrReceiverIdOrderByCreatedAtAsc(UUID senderId, UUID receiverId);

    long countByReceiverIdAndIsReadFalse(UUID receiverId);

    long countByConversationIdAndReceiverIdAndIsReadFalse(UUID conversationId, UUID receiverId);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = true, m.readAt = :now WHERE m.conversationId = :conversationId AND m.receiverId = :receiverId AND m.isRead = false")
    void markConversationMessagesAsRead(@Param("conversationId") UUID conversationId, @Param("receiverId") UUID receiverId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = true, m.readAt = :now WHERE m.conversationId = :conversationId AND m.senderId != :readerId AND m.isRead = false")
    void markAllIncomingAsRead(@Param("conversationId") UUID conversationId, @Param("readerId") UUID readerId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") UUID conversationId);
}
