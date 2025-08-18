package com.uniclub.domain.main.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "메인 페이지 미디어 업로드 요청 DTO")
@Getter
@NoArgsConstructor
public class MainMediaUploadRequestDto {
    @Schema(description = "미디어 URL", example = "uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png")
    @NotBlank(message = "미디어 URL을 입력해주세요.")
    private String mediaLink;
    
    @Schema(description = "미디어 타입", example = "MAIN_PAGE, CLUB_PROMOTION, CLUB_PROFILE, CLUB_BACKGROUND")
    @NotBlank(message = "미디어 타입을 지정해주세요.")
    private String mediaType;

    public Media toMediaEntity(MediaType mediatype) {
        return Media.builder()
                .mediaLink(mediaLink)
                .mediaType(mediatype)
                .build();
    }
}
