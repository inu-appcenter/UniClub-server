package com.uniclub.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final Long userId;
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final Long expiresIn;

    @Builder
    public LoginResponseDto(Long userId, String accessToken, Long expiresIn) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
