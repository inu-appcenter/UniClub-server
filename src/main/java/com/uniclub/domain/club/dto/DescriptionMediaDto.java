package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "동아리 홍보페이지 미디어 응답 DTO")
@Getter
public class DescriptionMediaDto {

    @Schema(description = "미디어 PK", example = "1")
    private final Long mediaId;

    @Schema(description = "미디어 링크 URL")
    private final String mediaLink;

    @Schema(description = "미디어 타입", example = "CLUB_PROMOTION")
    private final MediaType mediaType;

    @Schema(description = "메인 이미지 여부", example = "true")
    private final boolean main;

    @Schema(description = "수정 시각", example = "2025-08-10T15:30:00")
    private final LocalDateTime updatedAt;

    @Builder
    public DescriptionMediaDto(Long mediaId, String mediaLink, MediaType mediaType, boolean main, LocalDateTime updatedAt) {
        this.mediaId = mediaId;
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.main = main;
        this.updatedAt = updatedAt;
    }

    public static DescriptionMediaDto from(Media media, String presingedUrl) {
        return DescriptionMediaDto.builder()
                .mediaId(media.getMediaId())
                .mediaLink(presingedUrl)
                .mediaType(media.getMediaType())
                .main(media.isMainMedia())
                .updatedAt(media.getUpdateAt())
                .build();
    }
}
