package com.uniclub.domain.fcm.dto;

import com.uniclub.domain.fcm.entity.FcmToken;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "FCM 토큰 저장 요청 DTO")
@Getter
public class FcmRegisterRequestDto {
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String fcmToken;
}
