package com.uniclub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "알림설정 토글 응답 DTO")
@Getter
@AllArgsConstructor
public class ToggleNotificationResponseDto {

    @Schema(description = "처리 결과 메시지", example = "알림 설정이 변경되었습니다.")
    private final String message;

}