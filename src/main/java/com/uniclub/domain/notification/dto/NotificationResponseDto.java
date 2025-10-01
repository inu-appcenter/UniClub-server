package com.uniclub.domain.notification.dto;

import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "알림 조회 응답 DTO")
@Getter
public class NotificationResponseDto {

    @Schema(description = "알림 ID", example = "1")
    private final Long notificationId;

    @Schema(description = "알림 메시지", example = "질문에 답변이 도착했어요.")
    private final String message;

    @Schema(description = "읽음 여부", example = "false")
    private final boolean read;

    @Schema(description = "알림 종류", example = "PERSONAL")
    private final NotificationType notificationType;

    @Schema(description = "연결 해야하는 타켓의 ID")
    private final Long targetId;

    @Schema(description = "알림 생성 시간", example = "2025-08-03T14:30:00")
    private final LocalDateTime createdAt;

    @Builder
    public NotificationResponseDto(Long notificationId, String message, boolean read, NotificationType notificationType, Long targetId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.read = read;
        this.notificationType = notificationType;
        this.targetId = targetId;
        this.createdAt = createdAt;
    }

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .read(notification.isRead())
                .notificationType(notification.getNotificationType())
                .targetId(notification.getTargetId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
