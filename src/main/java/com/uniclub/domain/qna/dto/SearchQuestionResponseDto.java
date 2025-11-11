package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "QnA 페이지 조회 응답 DTO")
@Getter
public class SearchQuestionResponseDto {
    @Schema(description = "질문 ID", example = "1")
    private final Long questionId;
    
    @Schema(description = "질문 작성자 닉네임", example = "라면")
    private final String nickname;
    
    @Schema(description = "동아리명", example = "앱센터")
    private final String clubName;
    
    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    private final String content;
    
    @Schema(description = "답변 개수", example = "3")
    private final Long countAnswer;

    @Schema(description = "질문 수정 시간", example = "2025-08-25T10:30:00")
    private final LocalDateTime updatedAt;

    @Builder
    public SearchQuestionResponseDto(Long questionId, String nickname, String clubName, String content, Long countAnswer, LocalDateTime updatedAt) {
        this.questionId = questionId;
        this.nickname = nickname;
        this.clubName = clubName;
        this.content = content;
        this.countAnswer = countAnswer;
        this.updatedAt = updatedAt;
    }

    public static SearchQuestionResponseDto from(Question question, Long answerCount) {
        String displayName;
        if (question.isAnonymous()) {
            displayName = "익명";
        } else if (question.getUser() == null || question.getUser().isDeleted()) {
            displayName = "탈퇴한 사용자";
        } else {
            displayName = question.getUser().getNickname();
        }

        return SearchQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .nickname(displayName)
                .clubName(question.getClub().getName())
                .content(question.getContent())
                .countAnswer(answerCount)
                .updatedAt(question.getUpdateAt())
                .build();
    }

}
