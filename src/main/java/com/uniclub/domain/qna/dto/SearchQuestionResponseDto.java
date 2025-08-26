package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "QnA 페이지 조회 응답 DTO")
@Getter
public class SearchQuestionResponseDto {
    @Schema(description = "질문 ID", example = "1")
    private final Long questionId;
    
    @Schema(description = "질문 작성자명", example = "홍길동")
    private final String name;
    
    @Schema(description = "동아리명", example = "앱센터")
    private final String clubName;
    
    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    private final String content;
    
    @Schema(description = "답변 개수", example = "3")
    private final Long countAnswer;

    @Builder
    public SearchQuestionResponseDto(Long questionId, String name, String clubName, String content, Long countAnswer) {
        this.questionId = questionId;
        this.name = name;
        this.clubName = clubName;
        this.content = content;
        this.countAnswer = countAnswer;
    }

    public static SearchQuestionResponseDto from(Question question, Long answerCount) {
        String displayName = question.isAnonymous() ? "익명" : question.getUser().getName();

        return SearchQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .name(displayName)
                .clubName(question.getClub().getName())
                .content(question.getContent())
                .countAnswer(answerCount)
                .build();
    }

}
