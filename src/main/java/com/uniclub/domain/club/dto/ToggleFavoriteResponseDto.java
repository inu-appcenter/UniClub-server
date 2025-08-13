package com.uniclub.domain.club.dto;

import lombok.Getter;

@Getter
public class ToggleFavoriteResponseDto {
    private final String message;

    public ToggleFavoriteResponseDto(String message) {
        this.message = message;
    }
}
