package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "질문 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class QuestionUpdateRequestDto {
    private String content;

    private Boolean anonymous;

    private Boolean answered;

    public Question toEntity() {
        return Question.builder()
                .content(content)
                .anonymous(anonymous)
                .answered(answered)
                .build();
    }
}
