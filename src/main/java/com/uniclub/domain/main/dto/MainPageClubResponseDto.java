package com.uniclub.domain.main.dto;


import com.uniclub.domain.club.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainPageClubResponseDto {
    private final String name;
    private final String imageUrl;
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
