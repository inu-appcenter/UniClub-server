package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClubCreateRequestDto {

    @NotBlank
    private String name;    //동아리 이름


    //저장을 위해 Club Entity로 변환
    public Club toClubEntity(ClubCreateRequestDto clubCreateRequestDto) {
        return Club.builder()
                .name(clubCreateRequestDto.getName())
                .build();
    }
}
