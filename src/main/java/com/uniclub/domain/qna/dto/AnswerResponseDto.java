package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Answer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "질문 응답 DTO")
@Getter
public class AnswerResponseDto {

    @Schema(description = "답변 ID", example = "1")
    private final Long answerId;

    @Schema(description = "답변 작성자명", example = "홍길동")
    private final String name;

    @Schema(description = "답변 내용", example = "매 학기 초에 신입회원을 모집합니다.")
    private final String content;

    @Schema(description = "익명 여부", example = "false")
    private final boolean isAnonymous;

    @Schema(description = "답변 삭제 여부", example = "false")
    private final boolean isDeleted;

    @Schema(description = "답변 수정 시간", example = "2025-08-25T11:00:00")
    private final LocalDateTime updateTime;

    @Schema(description = "상위 답변 ID (대댓글인 경우)", example = "null")
    private final Long parentAnswerId;

    @Builder
    public AnswerResponseDto(Long answerId, String name, String content, boolean isAnonymous, boolean isDeleted, LocalDateTime updateTime, Long parentAnswerId) {
        this.answerId = answerId;
        this.name = name;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isDeleted = isDeleted;
        this.updateTime = updateTime;
        this.parentAnswerId = parentAnswerId;
    }

    public static AnswerResponseDto from(Answer answer) {
        String displayName = answer.isAnonymous() ? "익명" : answer.getUser().getName();

        return AnswerResponseDto.builder()
                .answerId(answer.getAnswerId())
                .name(displayName)
                .content(answer.getContent())
                .isAnonymous(answer.isAnonymous())
                .isDeleted(answer.isDeleted())
                .updateTime(answer.getUpdateAt())
                .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getAnswerId() : null)
                .build();
    }
}
