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
public class QuestionResponseDto extends BaseTime{

    @Schema(description = "질문 작성자명", example = "홍길동")
    private final String name;

    @Schema(description = "질문 작성자 ID", example = "123")
    private final Long userId;

    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    private final String content;

    @Schema(description = "익명 여부", example = "false")
    private final boolean isAnonymous;

    @Schema(description = "답변 완료 여부", example = "true")
    private final boolean isAnswered;

    @Schema(description = "질문 수정 시간", example = "2025-08-25T10:30:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "답변 목록")
    private final List<AnswerResponseDto> answers;

    @Builder
    public QuestionResponseDto(String name, Long userId, String content, boolean isAnonymous, boolean isAnswered, LocalDateTime updatedAt, List<AnswerResponseDto> answers) {
        this.name = name;
        this.userId = userId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isAnswered = isAnswered;
        this.updatedAt = updatedAt;
        this.answers = answers;
    }

    public static QuestionResponseDto from(Question question, List<AnswerResponseDto> answers) {
        String displayName = question.isAnonymous() ? "익명" : question.getUser().getName();

        return QuestionResponseDto.builder()
                .name(displayName)
                .userId(question.getUser().getUserId())
                .content(question.getContent())
                .isAnonymous(question.isAnonymous())
                .isAnswered(question.isAnswered())
                .updatedAt(question.getUpdateAt())
                .answers(answers)
                .build();

    }
}
