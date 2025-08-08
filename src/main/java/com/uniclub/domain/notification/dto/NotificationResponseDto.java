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

    @Schema(description = "알림 메시지", example = "질문에 답변이 도착했어요.")
    private final String message;

    @Schema(description = "읽음 여부", example = "false")
    private final boolean isRead;

    @Schema(description = "알림 종류", example = "PERSONAL")
    private final NotificationType type;

    @Schema(description = "알림 생성 시간", example = "2025-08-03T14:30:00")
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
                .isRead(notification.isRead())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
