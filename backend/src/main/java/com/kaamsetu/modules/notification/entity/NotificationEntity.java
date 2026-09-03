package com.kaamsetu.modules.notification.entity;

import com.kaamsetu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String category = "JOBS"; // 'JOBS' | 'APPLICATIONS' | 'SELECTIONS' | 'MESSAGES' | 'REMINDERS' | 'RATINGS_PAYMENTS' | 'SAFETY_ACCOUNT'

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "action_url", length = 300)
    private String actionUrl;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
}
