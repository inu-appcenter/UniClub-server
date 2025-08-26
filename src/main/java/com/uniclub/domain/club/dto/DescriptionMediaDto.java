package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DescriptionMediaDto {
    private final String mediaLink;
    private final MediaType mediaType;
    private final boolean isMain;
    private final LocalDateTime updatedAt;

    @Builder
    public DescriptionMediaDto(String mediaLink, MediaType mediaType, boolean isMain, LocalDateTime updatedAt) {
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.isMain = isMain;
        this.updatedAt = updatedAt;
    }

    public static DescriptionMediaDto from(Media media, String presingedUrl) {
        return DescriptionMediaDto.builder()
                .mediaLink(presingedUrl)
                .mediaType(media.getMediaType())
                .isMain(media.isMainMedia())
                .updatedAt(media.getUpdateAt())
                .build();
    }
}
