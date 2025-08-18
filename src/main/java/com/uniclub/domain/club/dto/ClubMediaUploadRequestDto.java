package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "동아리 미디어 업로드 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubMediaUploadRequestDto {
    @Schema(description = "미디어 URL", example = "https://example.com/media.jpg")
    @NotBlank(message = "미디어 URL을 입력해주세요.")
    private String mediaLink;
    
    @Schema(description = "미디어 타입", example = "CLUB_PROMOTION")
    @NotBlank(message = "미디어 타입을 지정해주세요.")
    private String mediaType;
    
    @Schema(description = "메인 이미지 여부", example = "false")
    @NotBlank(message = "메인 이미지 여부를 지정해주세요.")
    private boolean isMain;

    public Media toMediaEntity(Club club, MediaType type) {
        return Media.builder()
                .mediaLink(mediaLink)
                .mediaType(type)
                .isMain(isMain)
                .club(club)
                .build();
    }
}
