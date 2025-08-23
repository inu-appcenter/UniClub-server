package com.uniclub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림설정 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class NotificationSettingResponseDto {

    @Schema(description = "알림 활성화 여부", example = "true")
    private final boolean notificationEnabled;
}