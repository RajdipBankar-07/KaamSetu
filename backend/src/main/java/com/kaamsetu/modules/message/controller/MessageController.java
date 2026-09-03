package com.kaamsetu.modules.message.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.message.dto.*;
import com.kaamsetu.modules.message.service.MessageService;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messaging", description = "Secure ADMIN <-> USER Dedicated Communication API")
@SecurityRequirement(name = "BearerAuth")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/my-conversation")
    @Operation(summary = "Get current authenticated user's Admin conversation and messages")
    public ResponseEntity<ApiResponse<ConversationResponse>> getMyConversation(
            @AuthenticationPrincipal UserPrincipal principal) {
        ConversationResponse response = messageService.getMyConversation(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/send")
    @Operation(summary = "Send a secure message (Admin <-> User only, User-to-User forbidden)")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SendMessageRequest request) {
        RoleEnum role = principal.getRole();
        MessageResponse response = messageService.sendMessage(principal.getId(), role, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "message.sent", "Message sent successfully"));
    }

    @GetMapping("/admin/conversations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: List all user conversations with unread metrics and search")
    public ResponseEntity<ApiResponse<List<AdminConversationSummaryResponse>>> getAdminConversations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "ALL") String role,
            @RequestParam(required = false, defaultValue = "false") Boolean unreadOnly) {
        List<AdminConversationSummaryResponse> list = messageService.getAdminConversations(search, role, unreadOnly);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/admin/conversations/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Get conversation and message stream for a specific user")
    public ResponseEntity<ApiResponse<ConversationResponse>> getAdminConversationForUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID userId) {
        ConversationResponse response = messageService.getAdminConversationForUser(principal.getId(), userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get total unread messages count for current user")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {
        long count = messageService.getUnreadCount(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    @DeleteMapping("/clear/{targetId}")
    @Operation(summary = "Clear/archive conversation messages (Admin or User)")
    public ResponseEntity<ApiResponse<Void>> clearConversation(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID targetId) {
        messageService.clearConversation(principal.getId(), principal.getRole(), targetId);
        return ResponseEntity.ok(ApiResponse.ok(null, "message.cleared", "Conversation cleared successfully"));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear current authenticated user's Admin conversation")
    public ResponseEntity<ApiResponse<Void>> clearMyConversation(
            @AuthenticationPrincipal UserPrincipal principal) {
        messageService.clearConversation(principal.getId(), principal.getRole(), principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "message.cleared", "Conversation cleared successfully"));
    }
}

