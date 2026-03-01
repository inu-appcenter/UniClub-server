package com.uniclub.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "질문 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class QuestionUpdateRequestDto {

    @Schema(description = "수정할 질문 내용", example = "동아리 활동비는 얼마인가요?")
    @NotBlank(message = "질문 내용을 입력해주세요.")
    private String content;
}
