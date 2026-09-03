package com.kaamsetu.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSendMessageRequest {

    private UUID targetUserId;

    private String targetRole; // ALL, WORKER, PROVIDER

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message body is required")
    private String message;

    private String category; // SAFETY, DIRECT, ACCOUNT, JOB
}
