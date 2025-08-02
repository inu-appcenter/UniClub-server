package com.uniclub.domain.auth.dto;

import com.uniclub.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String accessToken;
    private final String tokenType = "Bearer";
    private Long expiresIn;
}
