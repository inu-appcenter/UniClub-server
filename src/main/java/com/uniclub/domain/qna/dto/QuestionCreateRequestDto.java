package com.uniclub.domain.qna.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "질문 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class QuestionCreateRequestDto {

    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    @NotBlank(message = "질문을 입력해주세요.")
    private String content;

    @Schema(description = "익명 여부", example = "false")
    @NotBlank(message = "익명 여부를 선택해주세요.")
    private boolean anonymous;

    public Question toQuestionEntity(User user, Club club) {
        return Question.builder()
                .content(content)
                .anonymous(anonymous)
                .user(user)
                .club(club)
                .build();
    }
}
