package com.kaamsetu.modules.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String senderRole;
    private UUID receiverId;
    private String receiverName;
    private String receiverRole;
    private String messageText;
    private boolean isRead;
    private Instant readAt;
    private Instant createdAt;
    private String timeDisplay;
}
