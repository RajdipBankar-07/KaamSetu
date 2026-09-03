package com.kaamsetu.modules.message.service;

import com.kaamsetu.modules.admin.service.AdminGovernanceService;
import com.kaamsetu.modules.message.dto.*;
import com.kaamsetu.modules.message.entity.ConversationEntity;
import com.kaamsetu.modules.message.entity.MessageEntity;
import com.kaamsetu.modules.message.repository.ConversationRepository;
import com.kaamsetu.modules.message.repository.MessageRepository;
import com.kaamsetu.modules.notification.service.NotificationService;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AdminGovernanceService adminGovernanceService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a")
            .withZone(ZoneId.of("Asia/Kolkata"));

    /**
     * Send a secure message with strict role-based destination authorization:
     * - WORKER / PROVIDER / PENDING USER can ONLY send to ADMIN.
     * - ADMIN can send to any registered user (WORKER, PROVIDER, PENDING).
     * - User-to-User messages (Worker <-> Worker, Worker <-> Provider) are HARD REJECTED.
     */
    @Transactional
    public MessageResponse sendMessage(UUID senderId, RoleEnum senderRole, SendMessageRequest request) {
        // 1. Sanitize and validate message text
        String rawText = request.getMessageText();
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty or blank");
        }
        String sanitizedText = sanitizeInput(rawText.trim());

        // 2. Identify the sender entity
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender user not found: " + senderId));

        UserEntity receiver;
        ConversationEntity conversation;

        if (senderRole == RoleEnum.ADMIN) {
            // ADMIN -> USER flow
            if (request.getReceiverId() == null) {
                throw new IllegalArgumentException("Receiver ID is required when Admin sends a message");
            }
            receiver = userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found: " + request.getReceiverId()));

            // Find or create Admin <-> User conversation
            UUID targetUserId = receiver.getId();
            conversation = conversationRepository.findByUserId(targetUserId)
                    .orElseGet(() -> conversationRepository.save(
                            ConversationEntity.builder()
                                     .adminUserId(senderId)
                                     .userId(targetUserId)
                                     .build()
                    ));
        } else {
            // USER -> ADMIN flow (Worker / Provider / Pending User)
            if (request.getReceiverId() != null) {
                UserEntity requestedReceiver = userRepository.findById(request.getReceiverId()).orElse(null);
                if (requestedReceiver != null && requestedReceiver.getRole() != RoleEnum.ADMIN) {
                    log.warn("SECURITY ALERT: User {} attempted direct user-to-user messaging to {}", senderId, request.getReceiverId());
                    throw new AccessDeniedException("Direct user-to-user messaging is strictly forbidden. You can only communicate with KaamSetu Admin.");
                }
            }

            // Find an active administrator to receive the message
            List<UserEntity> admins = userRepository.findByRole(RoleEnum.ADMIN);
            if (admins.isEmpty()) {
                throw new IllegalStateException("No administrator account found in system to receive message");
            }
            receiver = admins.get(0);

            // Find or create User <-> Admin conversation
            final UUID adminId = receiver.getId();
            conversation = conversationRepository.findByUserId(senderId)
                    .orElseGet(() -> conversationRepository.save(
                            ConversationEntity.builder()
                                    .adminUserId(adminId)
                                    .userId(senderId)
                                    .build()
                    ));
        }

        // 3. Persist the message
        MessageEntity message = MessageEntity.builder()
                .conversationId(conversation.getId())
                .senderId(sender.getId())
                .senderRole(senderRole)
                .receiverId(receiver.getId())
                .receiverRole(receiver.getRole())
                .messageText(sanitizedText)
                .isRead(false)
                .build();
        MessageEntity savedMessage = messageRepository.save(message);

        // 4. Update conversation metadata
        conversation.setLastMessageText(sanitizedText);
        conversation.setLastMessageSenderId(sender.getId());
        conversationRepository.save(conversation);

        // 5. Dispatch real-time notification
        try {
            String notifTitle = senderRole == RoleEnum.ADMIN
                    ? "🛡️ प्रशासनाकडून नवीन संदेश (Admin Message)"
                    : "💬 वापरकर्त्याचा संदेश: " + sender.getFullName();
            notificationService.createNotification(
                    receiver.getId(),
                    "MESSAGES",
                    notifTitle,
                    sanitizedText,
                    "/#messages"
            );
        } catch (Exception e) {
            log.warn("Could not dispatch notification for message {}: {}", savedMessage.getId(), e.getMessage());
        }

        // 6. Security Audit Log
        try {
            adminGovernanceService.logAudit(
                    sender.getId(),
                    "SEND_MESSAGE",
                    "MESSAGE",
                    savedMessage.getId(),
                    String.format("Message from %s (%s) to %s (%s)", sender.getFullName(), senderRole, receiver.getFullName(), receiver.getRole())
            );
        } catch (Exception ignored) {}

        return mapToMessageResponse(savedMessage, sender.getFullName(), senderRole.name(), receiver.getFullName(), receiver.getRole().name());
    }

    /**
     * Get or create authenticated user's Admin conversation and fetch message history.
     * Marks unread incoming admin messages as READ.
     */
    @Transactional
    public ConversationResponse getMyConversation(UUID userId) {
        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<UserEntity> admins = userRepository.findByRole(RoleEnum.ADMIN);
        UserEntity admin = admins.isEmpty() ? null : admins.get(0);
        UUID adminId = admin != null ? admin.getId() : UUID.randomUUID();
        String adminName = admin != null ? admin.getFullName() : "🛡️ प्रशासन (KaamSetu Admin)";

        ConversationEntity conversation = conversationRepository.findByUserId(userId)
                .orElseGet(() -> conversationRepository.save(
                        ConversationEntity.builder()
                                .adminUserId(adminId)
                                .userId(userId)
                                .lastMessageText("कामसेतू अधिकृत मदत कक्षामध्ये आपले स्वागत आहे.")
                                .build()
                ));

        // Mark unread incoming messages sent to this user as READ
        messageRepository.markAllIncomingAsRead(conversation.getId(), userId, Instant.now());

        List<MessageEntity> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        long unreadCount = messageRepository.countByConversationIdAndReceiverIdAndIsReadFalse(conversation.getId(), userId);

        List<MessageResponse> messageResponses = messages.stream().map(m -> {
            boolean isMe = m.getSenderId().equals(userId);
            String sName = isMe ? currentUser.getFullName() : adminName;
            String sRole = isMe ? currentUser.getRole().name() : "ADMIN";
            String rName = isMe ? adminName : currentUser.getFullName();
            String rRole = isMe ? "ADMIN" : currentUser.getRole().name();
            return mapToMessageResponse(m, sName, sRole, rName, rRole);
        }).collect(Collectors.toList());

        return ConversationResponse.builder()
                .conversationId(conversation.getId())
                .otherPartyUserId(adminId)
                .otherPartyName(adminName)
                .otherPartyRole("ADMIN")
                .otherPartyStatus("ACTIVE")
                .otherPartyAvatar("🛡️")
                .lastMessage(conversation.getLastMessageText())
                .lastMessageTime(conversation.getUpdatedAt())
                .unreadCount(unreadCount)
                .messages(messageResponses)
                .build();
    }

    /**
     * Admin view: Get all user conversations with unread badge metrics and search filters.
     */
    public List<AdminConversationSummaryResponse> getAdminConversations(String search, String roleFilter, Boolean unreadOnly) {
        List<UserEntity> allUsers = userRepository.findAll();
        List<AdminConversationSummaryResponse> summaries = new ArrayList<>();

        for (UserEntity user : allUsers) {
            if (user.getRole() == RoleEnum.ADMIN) continue; // Skip admin self

            // Apply search filtering
            if (search != null && !search.trim().isEmpty()) {
                String q = search.trim().toLowerCase();
                boolean matchName = user.getFullName() != null && user.getFullName().toLowerCase().contains(q);
                boolean matchUser = user.getUsername() != null && user.getUsername().toLowerCase().contains(q);
                boolean matchMobile = user.getMobile() != null && user.getMobile().contains(q);
                boolean matchEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(q);
                if (!matchName && !matchUser && !matchMobile && !matchEmail) {
                    continue;
                }
            }

            // Apply role filter
            if (roleFilter != null && !roleFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleFilter)) {
                String rf = roleFilter.trim().toUpperCase();
                if (rf.endsWith("S") && !rf.equals("STATUS")) {
                    rf = rf.substring(0, rf.length() - 1);
                }
                if (!user.getRole().name().equalsIgnoreCase(rf)) {
                    continue;
                }
            }

            Optional<ConversationEntity> convOpt = conversationRepository.findByUserId(user.getId());
            long unreadForAdmin = 0;
            String lastMsg = "नवीन वापरकर्ता (नोंदणी पूर्ण)";
            Instant lastTime = user.getCreatedAt();
            UUID convId = null;

            if (convOpt.isPresent()) {
                ConversationEntity conv = convOpt.get();
                convId = conv.getId();
                lastMsg = conv.getLastMessageText() != null ? conv.getLastMessageText() : lastMsg;
                lastTime = conv.getUpdatedAt();
                // Count unread messages sent by user to Admin
                unreadForAdmin = messageRepository.countByConversationIdAndReceiverIdAndIsReadFalse(conv.getId(), conv.getAdminUserId());
            }

            if (Boolean.TRUE.equals(unreadOnly) && unreadForAdmin == 0) {
                continue;
            }

            summaries.add(AdminConversationSummaryResponse.builder()
                    .conversationId(convId)
                    .userId(user.getId())
                    .userName(user.getFullName())
                    .userRole(user.getRole().name())
                    .userStatus(user.getStatus() != null ? user.getStatus().name() : "PENDING")
                    .userEmail(user.getEmail())
                    .userMobile(user.getMobile())
                    .userVillage(user.getVillage() != null ? user.getVillage() : "शिरूर")
                    .lastMessage(lastMsg)
                    .lastMessageTime(lastTime)
                    .unreadCount(unreadForAdmin)
                    .build());
        }

        // Sort by unread first, then latest activity
        summaries.sort((a, b) -> {
            if (a.getUnreadCount() > 0 && b.getUnreadCount() == 0) return -1;
            if (a.getUnreadCount() == 0 && b.getUnreadCount() > 0) return 1;
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return summaries;
    }

    /**
     * Admin view: Get message stream for a specific user.
     * Automatically marks messages sent by the user as READ.
     */
    @Transactional
    public ConversationResponse getAdminConversationForUser(UUID adminId, UUID targetUserId) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUserId));

        ConversationEntity conversation = conversationRepository.findByUserId(targetUserId)
                .orElseGet(() -> conversationRepository.save(
                        ConversationEntity.builder()
                                .adminUserId(adminId)
                                .userId(targetUserId)
                                .lastMessageText("नवीन संवाद सुरू झाला.")
                                .build()
                ));

        // Mark user's messages as read by Admin
        messageRepository.markAllIncomingAsRead(conversation.getId(), adminId, Instant.now());

        List<MessageEntity> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        List<MessageResponse> messageResponses = messages.stream().map(m -> {
            boolean isAdminMsg = m.getSenderId().equals(adminId);
            String sName = isAdminMsg ? "🛡️ प्रशासन (KaamSetu Admin)" : targetUser.getFullName();
            String sRole = isAdminMsg ? "ADMIN" : targetUser.getRole().name();
            String rName = isAdminMsg ? targetUser.getFullName() : "🛡️ प्रशासन (KaamSetu Admin)";
            String rRole = isAdminMsg ? targetUser.getRole().name() : "ADMIN";
            return mapToMessageResponse(m, sName, sRole, rName, rRole);
        }).collect(Collectors.toList());

        return ConversationResponse.builder()
                .conversationId(conversation.getId())
                .otherPartyUserId(targetUser.getId())
                .otherPartyName(targetUser.getFullName())
                .otherPartyRole(targetUser.getRole().name())
                .otherPartyStatus(targetUser.getStatus() != null ? targetUser.getStatus().name() : "PENDING")
                .otherPartyAvatar(targetUser.getRole() == RoleEnum.WORKER ? "👷" : "👤")
                .lastMessage(conversation.getLastMessageText())
                .lastMessageTime(conversation.getUpdatedAt())
                .unreadCount(0)
                .messages(messageResponses)
                .build();
    }

    /**
     * Clear / archive messages for a conversation.
     */
    @Transactional
    public void clearConversation(UUID callerId, RoleEnum callerRole, UUID targetId) {
        ConversationEntity conversation;
        if (callerRole == RoleEnum.ADMIN) {
            // Target can be targetUserId or conversationId
            conversation = conversationRepository.findByUserId(targetId)
                    .or(() -> conversationRepository.findById(targetId))
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found for target: " + targetId));
            conversation.setLastMessageText("संभाषण इतिहास साफ केला.");
        } else {
            // User clears their own conversation
            conversation = conversationRepository.findByUserId(callerId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found for user: " + callerId));
            conversation.setLastMessageText("कामसेतू अधिकृत मदत कक्षामध्ये आपले स्वागत आहे.");
        }

        messageRepository.deleteByConversationId(conversation.getId());
        conversationRepository.save(conversation);

        try {
            adminGovernanceService.logAudit(
                    callerId,
                    "CLEAR_CHAT",
                    "CONVERSATION",
                    conversation.getId(),
                    String.format("Chat cleared by %s (%s)", callerId, callerRole)
            );
        } catch (Exception ignored) {}
    }

    /**
     * Get total unread count for the authenticated user.
     */
    public long getUnreadCount(UUID userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    private MessageResponse mapToMessageResponse(MessageEntity entity, String senderName, String senderRole, String receiverName, String receiverRole) {
        String timeStr = "Just now";
        if (entity.getCreatedAt() != null) {
            try {
                timeStr = TIME_FORMATTER.format(entity.getCreatedAt());
            } catch (Exception e) {
                timeStr = entity.getCreatedAt().toString();
            }
        }

        return MessageResponse.builder()
                .id(entity.getId())
                .conversationId(entity.getConversationId())
                .senderId(entity.getSenderId())
                .senderName(senderName)
                .senderRole(senderRole)
                .receiverId(entity.getReceiverId())
                .receiverName(receiverName)
                .receiverRole(receiverRole)
                .messageText(entity.getMessageText())
                .isRead(entity.getIsRead())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .timeDisplay(timeStr)
                .build();
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        return input
                .replaceAll("(?i)<script.*?>.*?</script.*?>", "")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
}
