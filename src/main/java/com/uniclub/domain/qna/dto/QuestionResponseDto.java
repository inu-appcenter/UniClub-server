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

    @Schema(description = "질문 ID", example = "1")
    private final Long questionId;

    @Schema(description = "질문 작성자명", example = "홍길동")
    private final String name;

    @Schema(description = "질문 작성자 ID", example = "123")
    private final Long userId;

    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    private final String content;

    @Schema(description = "익명 여부", example = "false")
    private final boolean anonymous;

    @Schema(description = "답변 완료 여부", example = "true")
    private final boolean answered;

    @Schema(description = "질문 수정 시간", example = "2025-08-25T10:30:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "답변 목록")
    private final List<AnswerResponseDto> answers;

    @Builder
    public QuestionResponseDto(Long questionId, String name, Long userId, String content, boolean anonymous, boolean answered, LocalDateTime updatedAt, List<AnswerResponseDto> answers) {
        this.questionId = questionId;
        this.name = name;
        this.userId = userId;
        this.content = content;
        this.anonymous = anonymous;
        this.answered = answered;
        this.updatedAt = updatedAt;
        this.answers = answers;
    }

    public static QuestionResponseDto from(Question question, List<AnswerResponseDto> answers) {
        String displayName;
        if (question.isAnonymous()) {
            displayName = "익명";
        } else if (question.getUser() == null || question.getUser().isDeleted()) {
            displayName = "탈퇴한 사용자";
        } else {
            displayName = question.getUser().getName();
        }

        return QuestionResponseDto.builder()
                .questionId(question.getQuestionId())
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
