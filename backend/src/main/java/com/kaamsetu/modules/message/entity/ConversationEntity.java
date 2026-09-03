package com.kaamsetu.modules.message.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "conversations",
    indexes = {
        @Index(name = "idx_conv_user", columnList = "user_id"),
        @Index(name = "idx_conv_admin", columnList = "admin_user_id"),
        @Index(name = "idx_conv_admin_user", columnList = "admin_user_id, user_id", unique = true)
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationEntity extends BaseEntity {

    @Column(name = "admin_user_id", nullable = false)
    private UUID adminUserId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "last_message_text", length = 1000)
    private String lastMessageText;

    @Column(name = "last_message_sender_id")
    private UUID lastMessageSenderId;
}
