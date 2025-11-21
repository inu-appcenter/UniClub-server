package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "질문 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class QuestionUpdateRequestDto {

    @Schema(description = "수정할 질문 내용", example = "동아리 활동비는 얼마인가요?")
    @NotBlank(message = "질문 내용을 입력해주세요.")
    private String content;

    @Schema(description = "익명 여부", example = "false")
    @NotNull(message = "익명 여부를 선택해주세요.")
    private Boolean anonymous;

    public Question toEntity() {
        return Question.builder()
                .content(content)
                .anonymous(anonymous)
                .build();
    }
}
