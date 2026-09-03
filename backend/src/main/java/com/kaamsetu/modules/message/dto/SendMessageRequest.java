package com.kaamsetu.modules.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    /**
     * Optional receiver user ID.
     * When sent by WORKER/PROVIDER, this is ignored or must be Admin.
     * When sent by ADMIN, this specifies the target recipient user ID.
     */
    private UUID receiverId;

    @NotBlank(message = "Message text cannot be empty or whitespace only")
    @Size(min = 1, max = 2000, message = "Message must be between 1 and 2000 characters")
    private String messageText;
}
