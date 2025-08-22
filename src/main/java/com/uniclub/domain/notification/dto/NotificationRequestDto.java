package com.uniclub.domain.notification.dto;

import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import com.uniclub.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Schema(description = "알림 생성 요청 DTO")
@Getter
public class NotificationRequestDto {

    @Schema(description = "알림 메시지", example = "질문에 답변이 도착했어요.")
    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;

    @Schema(description = "알림 타입", example = "SYSTEM")
    @NotBlank(message = "알림 타입을 지정해주세요.")
    private String notificationType;

    @Schema(description = "알림을 받을 유저", example = "[\"202012345\"]")
    @NotEmpty(message = "알림을 받을 유저를 명시해주세요.")
    private List<@NotBlank(message = "학번은 빈 값일 수 없습니다.") String> studentIds;

    // 저장을 위해 알림 객체 생성
    public Notification toNotificationEntity(User user, NotificationType type) {
        return Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .build();
    }
}
