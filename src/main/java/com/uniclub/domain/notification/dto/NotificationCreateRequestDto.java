package com.uniclub.domain.notification.dto;

import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

//테스트 용도 개발
@Schema(description = "알림 생성 요청 테스트 DTO")
@Getter
public class NotificationCreateRequestDto {

    @Schema(description = "알림 제목")
    @NotBlank(message = "제목 입력해주세요.")
    private String title;

    @Schema(description = "알림 메시지", example = "질문에 답변이 도착했어요.")
    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;

    @Schema(description = "알림 타입", example = "SYSTEM, CLUB, QNA")
    @NotBlank(message = "알림 타입을 지정해주세요.")
    private String notificationType;

    @Schema(description = "알림 타입에 따른 아이디를 입력", example = "1")
    @NotBlank(message = "타겟 아이디는 필수입니다.")
    private Long targetId;

    @Schema(description = "알림을 받는 유저", example = "1")
    @NotBlank(message = "유저 아이디를 지정해주세요")
    private Long userId;

    public Notification toEntity(NotificationType notificationType) {
        return Notification.builder()
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .targetId(targetId)
                .userId(userId)
                .build();
    }
}
