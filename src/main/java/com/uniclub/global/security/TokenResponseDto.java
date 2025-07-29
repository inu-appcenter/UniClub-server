package com.uniclub.global.security;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponseDto {
    private final String token;

    @Builder
    public TokenResponseDto(String token) {
        this.token = token;
    }
}
