package com.uniclub.domain.main.dto;

import com.uniclub.domain.club.dto.DescriptionMediaDto;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "메인페이지 미디어 응답 DTO")
@Getter
public class MainPageMediaResponseDto {
    @Schema(description = "미디어 링크 URL", example = "https://example.com/main-image.jpg")
    private final String mediaLink;
    
    @Schema(description = "미디어 타입", example = "MAIN_PAGE")
    private final MediaType mediaType;

    @Builder
    public MainPageMediaResponseDto(String mediaLink, MediaType mediaType) {
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
    }

    public static MainPageMediaResponseDto from(Media media) {
        return MainPageMediaResponseDto.builder()
                .mediaLink(media.getMediaLink())
                .mediaType(media.getMediaType())
                .build();
    }

}
