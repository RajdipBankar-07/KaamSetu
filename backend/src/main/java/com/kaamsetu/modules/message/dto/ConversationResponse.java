package com.kaamsetu.modules.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private UUID conversationId;
    private UUID otherPartyUserId;
    private String otherPartyName;
    private String otherPartyRole;
    private String otherPartyStatus;
    private String otherPartyAvatar;
    private String lastMessage;
    private Instant lastMessageTime;
    private long unreadCount;
    private List<MessageResponse> messages;
}
