package com.uniclub.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "페이징 응답 DTO")
@Getter
public class PageQuestionResponseDto<T> {
    @Schema(description = "조회된 질문 목록")
    private final List<T> content;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private final boolean hasNext;

    @Builder
    public PageQuestionResponseDto(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
