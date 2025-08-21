package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "질문 조회 DTO")
@Getter
public class QuestionResponseDto {

    private final boolean isOwner;

    private final String content;

    private final boolean isAnonymous;

    private final boolean isAnswered;

    @Builder
    public QuestionResponseDto(boolean isOwner, String content, boolean isAnonymous, boolean isAnswered) {
        this.isOwner = isOwner;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isAnswered = isAnswered;
    }

    public static QuestionResponseDto from(boolean isOwner, Question question) {
        return QuestionResponseDto.builder()
                .isOwner(isOwner)
                .content(question.getContent())
                .isAnonymous(question.isAnonymous())
                .isAnswered(question.isAnswered())
                .build();

    }
}
