package com.kaamsetu.modules.notification.service;

import com.kaamsetu.modules.notification.entity.NotificationEntity;
import com.kaamsetu.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationEntity createNotification(UUID userId, String category, String title, String message, String actionUrl) {
        NotificationEntity notification = NotificationEntity.builder()
                .userId(userId)
                .category(category != null ? category : "JOBS")
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<NotificationEntity> getUserNotifications(UUID userId, String category) {
        if (category != null && !category.equalsIgnoreCase("all")) {
            return notificationRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, category.toUpperCase());
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationEntity markAsRead(UUID userId, UUID notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized: You do not own this notification");
        }

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }
}
