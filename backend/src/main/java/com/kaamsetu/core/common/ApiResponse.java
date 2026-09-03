package com.kaamsetu.core.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private boolean success = true;

    private String message;

    private String messageKey;

    private T data;

    private String errorCode;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> ok(T data, String messageKey, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .messageKey(messageKey)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String messageKey, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .messageKey(messageKey)
                .message(message)
                .build();
    }
}
