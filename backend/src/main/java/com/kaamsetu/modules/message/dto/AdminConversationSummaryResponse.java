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
public class AdminConversationSummaryResponse {

    private UUID conversationId;
    private UUID userId;
    private String userName;
    private String userRole;
    private String userStatus;
    private String userEmail;
    private String userMobile;
    private String userVillage;
    private String lastMessage;
    private Instant lastMessageTime;
    private long unreadCount;
}
