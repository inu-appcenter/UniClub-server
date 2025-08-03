package com.uniclub.domain.notification.dto;

import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private final String message;

    private final boolean isRead;

    private final NotificationType type;

    private final LocalDateTime createdAt;

    @Builder
    public NotificationResponseDto(String message, boolean isRead, NotificationType type, LocalDateTime createdAt) {
        this.message = message;
        this.isRead = isRead;
        this.type = type;
        this.createdAt = createdAt;
    }

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
