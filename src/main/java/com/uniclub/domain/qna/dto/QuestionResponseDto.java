package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import com.uniclub.global.util.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "질문 조회 DTO")
@Getter
public class QuestionResponseDto extends BaseTime{

    private final String name;

    private final boolean isOwner;

    private final String content;

    private final boolean isAnonymous;

    private final boolean isAnswered;

    private final LocalDateTime updatedAt;


    @Builder
    public QuestionResponseDto(String name, boolean isOwner, String content, boolean isAnonymous, boolean isAnswered, LocalDateTime updatedAt) {
        this.name = name;
        this.isOwner = isOwner;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isAnswered = isAnswered;
        this.updatedAt = updatedAt;
    }

    public static QuestionResponseDto from(boolean isOwner, Question question) {
        return QuestionResponseDto.builder()
                .name(question.getUser().getName())
                .isOwner(isOwner)
                .content(question.getContent())
                .isAnonymous(question.isAnonymous())
                .isAnswered(question.isAnswered())
                .updatedAt(question.getUpdateAt())
                .build();

    }
}
