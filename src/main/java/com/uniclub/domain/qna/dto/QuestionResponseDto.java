package com.uniclub.domain.qna.dto;

import com.uniclub.domain.qna.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "질문 조회 DTO")
@Getter
public class QuestionResponseDto {

    @Schema(description = "질문 ID", example = "1")
    private final Long questionId;

    @Schema(description = "질문 작성자 닉네임", example = "라면")
    private final String nickname;

    @Schema(description = "동아리 이름", example = "앱센터")
    private final String clubName;

    @Schema(description = "질문 내용", example = "동아리원 모집은 언제 진행하나요?")
    private final String content;

    @Schema(description = "익명 여부", example = "false")
    private final boolean anonymous;

    @Schema(description = "답변 완료 여부", example = "true")
    private final boolean answered;

    @Schema(description = "질문 수정 시간", example = "2025-08-25T10:30:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "본인 질문 여부", example = "true")
    private final boolean owner;

    @Schema(description = "질문자 프로필")
    private final String profile;

    @Schema(description = "동아리 회장 여부", example = "false")
    private final boolean president;

    @Schema(description = "답변 목록")
    private final List<AnswerResponseDto> answers;

    @Builder
    public QuestionResponseDto(Long questionId, String nickname, String clubName, String content, boolean anonymous, boolean answered, LocalDateTime updatedAt, List<AnswerResponseDto> answers, boolean owner, String profile, boolean president) {
        this.questionId = questionId;
        this.nickname = nickname;
        this.clubName = clubName;
        this.content = content;
        this.anonymous = anonymous;
        this.answered = answered;
        this.updatedAt = updatedAt;
        this.answers = answers;
        this.owner = owner;
        this.profile = profile;
        this.president = president;
    }

    public static QuestionResponseDto from(Question question, String displayName, List<AnswerResponseDto> answers, boolean owner, String displayProfile, boolean president) {
        return QuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .nickname(displayName)
                .content(question.getContent())
                .clubName(question.getClub().getName())
                .anonymous(question.isAnonymous())
                .answered(question.isAnswered())
                .updatedAt(question.getUpdateAt())
                .answers(answers)
                .owner(owner)
                .profile(displayProfile)
                .president(president)
                .build();
    }
}
