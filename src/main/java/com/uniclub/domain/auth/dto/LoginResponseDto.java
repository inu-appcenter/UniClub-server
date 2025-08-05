package com.uniclub.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "로그인 응답 DTO")
@Getter
public class LoginResponseDto {
    @Schema(description = "유저 PK", example = "1")
    private final Long userId;

    @Schema(description = "액세스 토큰값", example = "6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B")
    private final String accessToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private final String tokenType = "Bearer";

    @Schema(description = "토큰 유효시간", example = "3600")
    private final Long expiresIn;

    @Builder
    public LoginResponseDto(Long userId, String accessToken, Long expiresIn) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
