package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "답변 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class AnswerCreateRequestDto {

    @Schema(description = "답변 내용", example = "매 학기 초에 신입회원을 모집합니다.")
    @NotBlank(message = "답변을 입력해주세요.")
    private String content;

    @Schema(description = "익명 여부", example = "false")
    @NotBlank(message = "익명 여부를 선택해주세요.")
    private boolean isAnonymous;

    public Answer toEntity(UserDetailsImpl userDetails, Question question, Answer parentAnswer) {
        return Answer.builder()
                .content(content)
                .isAnonymous(isAnonymous)
                .question(question)
                .parentAnswer(parentAnswer)
                .build();
    }
}
