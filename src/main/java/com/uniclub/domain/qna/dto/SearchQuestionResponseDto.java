package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Qna 페이지 조회 응답 DTO")
@Getter
public class SearchQuestionResponseDto {
    private final String name;
    private final String clubName;
    private final String content;
    private final Long countAnswer;

    @Builder
    public SearchQuestionResponseDto(String name, String clubName, String content, Long countAnswer) {
        this.name = name;
        this.clubName = clubName;
        this.content = content;
        this.countAnswer = countAnswer;
    }

    public static SearchQuestionResponseDto from(Question question, Long answerCount) {
        String displayName = question.isAnonymous() ? "익명" : question.getUser().getName();

        return SearchQuestionResponseDto.builder()
                .name(displayName)
                .clubName(question.getClub().getName())
                .content(question.getContent())
                .countAnswer(answerCount)
                .build();
    }

}
