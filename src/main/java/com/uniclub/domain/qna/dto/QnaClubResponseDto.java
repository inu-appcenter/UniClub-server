package com.uniclub.domain.qna.dto;


import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Qna 페이지 동아리 목록 응답 DTO")
@Getter
public class QnaClubResponseDto {
    private final Long clubId;
    private final String clubName;
    private final CategoryType categoryType;

    @Builder
    public QnaClubResponseDto(Long clubId, String clubName, CategoryType category) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.categoryType = category;
    }

    public static QnaClubResponseDto from(Long clubId, String clubName, CategoryType categoryType) {
        return QnaClubResponseDto.builder()
                .clubId(clubId)
                .clubName(clubName)
                .category(categoryType)
                .build();
    }
}
