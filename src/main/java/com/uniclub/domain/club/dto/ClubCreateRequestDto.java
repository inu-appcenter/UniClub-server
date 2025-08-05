package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "동아리 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubCreateRequestDto {

    @Schema(description = "동아리 이름", example = "앱센터")
    @NotBlank(message = "동아리 이름을 입력해주세요.")
    private String name;    //동아리 이름


    //저장을 위해 Club Entity로 변환
    public Club toClubEntity(ClubCreateRequestDto clubCreateRequestDto) {
        return Club.builder()
                .name(clubCreateRequestDto.getName())
                .build();
    }
}
