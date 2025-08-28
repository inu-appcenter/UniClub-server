package com.uniclub.domain.club.dto;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Schema(description = "동아리 조회 응답 DTO")
@Getter
public class ClubResponseDto {

    @Schema(description = "동아리 PK", example = "1")
    private final Long id;

    @Schema(description = "동아리 이름", example = "앱센터")
    private final String name;

    @Schema(description = "동아리 소개글")
    private final String info;

    @Schema(description = "동아리 모집 상태", example = "CLOSED")
    private final ClubStatus status;

    @Schema(description = "관심동아리 여부", example = "true")
    private final boolean favorite;

    @Schema(description = "동아리 카테고리", example = "SPORTS")
    private final Category category;

    @Builder
    private ClubResponseDto(Long id, String name, String info, ClubStatus status, boolean favorite, Category category) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.status = status;
        this.favorite = favorite;
        this.category = category;
    }

    public static ClubResponseDto from(Club club, boolean favorite) {
        return ClubResponseDto.builder()
                .id(club.getClubId())
                .name(club.getName())
                .info(club.getSimpleDescription())
                .status(club.getStatus())
                .favorite(favorite)
                .category(club.getCategory())
                .build();
    }
}
