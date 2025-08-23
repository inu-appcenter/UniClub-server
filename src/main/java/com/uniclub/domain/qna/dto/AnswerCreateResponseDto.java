package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Answer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "답변 생성 응답 DTO")
@Getter
public class AnswerCreateResponseDto {

    private final Long answerId;

    @Builder
    public AnswerCreateResponseDto(Long answerId) {
        this.answerId = answerId;
    }

    public static AnswerCreateResponseDto from(Answer answer) {
        return AnswerCreateResponseDto.builder()
                .answerId(answer.getAnswerId())
                .build();
    }
}
