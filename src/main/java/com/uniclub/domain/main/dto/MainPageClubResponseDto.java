package com.uniclub.domain.main.dto;


import com.uniclub.domain.club.entity.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "메인페이지 동아리 응답 DTO")
@Getter
public class MainPageClubResponseDto {
    @Schema(description = "동아리 이름", example = "앱센터")
    private final String name;
    
    @Schema(description = "동아리 이미지 URL", example = "https://example.com/club-image.jpg")
    private final String imageUrl;
    
    @Schema(description = "관심동아리 여부", example = "true")
    private final boolean favorite;

    @Builder
    public MainPageClubResponseDto(String name, String imageUrl, boolean favorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.favorite = favorite;
    }

    /*
    public static MainPageClubResponseDto from(Club club, boolean isFavorite) {
        return MainPageClubResponseDto.builder()
                .name(club.getName())
                .imageUrl()
                .favorite(isFavorite).build();
    }
    */
}
