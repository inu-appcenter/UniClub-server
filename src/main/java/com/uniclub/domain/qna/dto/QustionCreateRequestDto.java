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
public class QustionCreateRequestDto {

    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    @NotBlank(message = "질문을 입력해주세요.")
    private String content;

    @Schema(description = "익명 여부", example = "false")
    private boolean isAnnonymous;

    public Question toQuestionEntity(User user, Club club) {
        return Question.builder()
                .content(content)
                .isAnonymous(isAnnonymous)
                .user(user)
                .club(club)
                .build();
    }
}
