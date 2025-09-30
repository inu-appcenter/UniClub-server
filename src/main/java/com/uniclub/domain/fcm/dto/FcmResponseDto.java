package com.uniclub.domain.fcm.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FcmResponseDto {
    private final int successCount;
    private final int failureCount;

    @Builder
    public FcmResponseDto(int successCount, int failureCount, List<String> failedTokens) {
        this.successCount = successCount;
        this.failureCount = failureCount;
    }
}
