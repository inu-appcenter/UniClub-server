package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "질문 생성 응답 DTO")
@Getter
public class QuestionCreateResponseDto {
    @Schema(description = "생성된 질문 ID", example = "123")
    private final Long questionId;

    @Builder
    public QuestionCreateResponseDto(Long questionId) {
        this.questionId = questionId;
    }

    public static QuestionCreateResponseDto from(Question question) {
        return QuestionCreateResponseDto.builder()
                .questionId(question.getQuestionId())
                .build();
    }
}
