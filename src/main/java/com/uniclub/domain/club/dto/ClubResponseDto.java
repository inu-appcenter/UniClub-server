package com.uniclub.domain.club.dto;

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
    private Long id;

    @Schema(description = "동아리 이름", example = "앱센터")
    private String name;

    @Schema(description = "동아리 소개글")
    private String info;

    @Schema(description = "동아리 모집 상태", example = "CLOSED")
    private ClubStatus status;

    @Schema(description = "관심동아리 여부", example = "true")
    private boolean favorite;

    @Builder
    private ClubResponseDto(Long id, String name, String info, ClubStatus status, boolean favorite) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.status = status;
        this.favorite = favorite;
    }

    public static ClubResponseDto from(Club club, boolean favorite) {
        return ClubResponseDto.builder()
                .id(club.getClubId())
                .name(club.getName())
                .info(club.getDescription())
                .status(club.getStatus())
                .favorite(favorite)
                .build();
    }
}
