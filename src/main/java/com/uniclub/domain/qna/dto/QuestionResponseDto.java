package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "질문 조회 DTO")
@Getter
public class QuestionResponseDto {

    private final String name;

    private final Long userId;

    private final String content;

    private final boolean anonymous;

    private final boolean answered;

    private final LocalDateTime updatedAt;

    private final List<AnswerResponseDto> answers;

    @Builder
    public QuestionResponseDto(String name, Long userId, String content, boolean anonymous, boolean answered, LocalDateTime updatedAt, List<AnswerResponseDto> answers) {
        this.name = name;
        this.userId = userId;
        this.content = content;
        this.anonymous = anonymous;
        this.answered = answered;
        this.updatedAt = updatedAt;
        this.answers = answers;
    }

    public static QuestionResponseDto from(Question question, List<AnswerResponseDto> answers) {
        String displayName = question.isAnonymous() ? "익명" : question.getUser().getName();

        return QuestionResponseDto.builder()
                .name(displayName)
                .userId(question.getUser().getUserId())
                .content(question.getContent())
                .anonymous(question.isAnonymous())
                .answered(question.isAnswered())
                .updatedAt(question.getUpdateAt())
                .answers(answers)
                .build();

    }
}
