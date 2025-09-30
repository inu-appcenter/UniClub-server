package com.uniclub.domain.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

//추후 클라이언트에서 알림 직접 보낼때 사용
@Getter
public class FcmNotificationResponseDto {
    @Schema(description = "성공 여부")
    private final boolean success;

    @Schema(description = "응답 메시지")
    private final String message;

    @Schema(description = "FCM 전송 결과")
    private final FcmResponseDto fcmResponseDto;

    @Builder
    public FcmNotificationResponseDto(boolean success, String message, FcmResponseDto fcmResponseDto) {
        this.success = success;
        this.message = message;
        this.fcmResponseDto = fcmResponseDto;
    }
}
