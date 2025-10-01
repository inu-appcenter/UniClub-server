package com.uniclub.domain.fcm.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FcmMessageDto {
    private final List<String> tokens;
    private final String title;
    private final String content;

    @Builder
    public FcmMessageDto(List<String> tokens, String title, String content) {
        this.tokens = tokens;
        this.title = title;
        this.content = content;
    }
}
