package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubResponseDto {
    private Long id;
    private String name;
    private String info;
    private ClubStatus status;
    private boolean favorite;

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
