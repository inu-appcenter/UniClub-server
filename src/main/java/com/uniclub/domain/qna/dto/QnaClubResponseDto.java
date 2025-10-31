package com.uniclub.domain.qna.dto;


import com.uniclub.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Qna 페이지 동아리 목록 응답 DTO")
@Getter
public class QnaClubResponseDto {
    private final Long clubId;
    private final String clubName;
    private final Category category;

    @Builder
    public QnaClubResponseDto(Long clubId, String clubName, Category category) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.category = category;
    }

    public static QnaClubResponseDto from(Long clubId, String clubName, Category category) {
        return QnaClubResponseDto.builder()
                .clubId(clubId)
                .clubName(clubName)
                .category(category)
                .build();
    }
}
