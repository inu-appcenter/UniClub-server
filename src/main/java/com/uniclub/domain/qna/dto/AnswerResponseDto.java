package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Answer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "질문 응답 DTO")
@Getter
public class AnswerResponseDto {

    private final Long answerId;

    private final String name;

    private final String content;

    private final boolean anoneymous;

    private final LocalDateTime updateTime;

    private final Long parentAnswerId;

    @Builder
    public AnswerResponseDto(Long answerId, String name, String content, boolean anoneymous, LocalDateTime updateTime, Long parentAnswerId) {
        this.answerId = answerId;
        this.name = name;
        this.content = content;
        this.anoneymous = anoneymous;
        this.updateTime = updateTime;
        this.parentAnswerId = parentAnswerId;
    }

    public static AnswerResponseDto from(Answer answer) {
        String displayName;
        if (answer.getUser() == null) {   //삭제된 사용자의 답변
            displayName = "(알 수 없음)";
        } else {    //익명처리
            displayName = answer.isAnonymous() ? "익명" : answer.getUser().getName();
        }

        return AnswerResponseDto.builder()
                .answerId(answer.getAnswerId())
                .name(displayName)
                .content(answer.getContent())
                .anoneymous(answer.isAnonymous())
                .updateTime(answer.getUpdateAt())
                .parentAnswerId(answer.getParentAnswer().getAnswerId())
                .build();
    }
}
