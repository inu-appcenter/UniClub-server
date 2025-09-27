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

    @Schema(description = "답변 작성자 닉네임", example = "라면")
    private final String nickname;

    @Schema(description = "답변 내용", example = "매 학기 초에 신입회원을 모집합니다.")
    private final String content;

    @Schema(description = "익명 여부", example = "false")
    private final boolean anonymous;

    @Schema(description = "답변 삭제 여부", example = "false")
    private final boolean deleted;

    @Schema(description = "답변 수정 시간", example = "2025-08-25T11:00:00")
    private final LocalDateTime updateTime;

    @Schema(description = "상위 답변 ID (대댓글인 경우)", example = "null")
    private final Long parentAnswerId;

    @Schema(description = "본인 답변 여부", example = "true")
    private final boolean owner;

    @Builder
    public AnswerResponseDto(Long answerId, String nickname, String content, boolean anonymous, boolean deleted, LocalDateTime updateTime, Long parentAnswerId, boolean owner) {
        this.answerId = answerId;
        this.nickname = nickname;
        this.content = content;
        this.anonymous = anonymous;
        this.deleted = deleted;
        this.updateTime = updateTime;
        this.parentAnswerId = parentAnswerId;
        this.owner = owner;
    }


    public static AnswerResponseDto from(Answer answer, String displayName, boolean owner) {

        return AnswerResponseDto.builder()
                .answerId(answer.getAnswerId())
                .nickname(displayName)
                .content(answer.getContent())
                .anonymous(answer.isAnonymous())
                .deleted(answer.isDeleted())
                .updateTime(answer.getUpdateAt())
                .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getAnswerId() : null)
                .owner(owner)
                .build();
    }
}
