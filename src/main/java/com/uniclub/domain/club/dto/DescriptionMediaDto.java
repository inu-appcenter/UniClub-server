package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DescriptionMediaDto {
    private final String mediaLink;
    private final MediaType mediaType;
    private final boolean isMain;

    @Builder
    public DescriptionMediaDto(String mediaLink, MediaType mediaType, boolean isMain) {
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.isMain = isMain;
    }

    public static DescriptionMediaDto from(Media media) {
        return DescriptionMediaDto.builder()
                .mediaLink(media.getMediaLink())
                .mediaType(media.getMediaType())
                .isMain(media.isMain())
                .build();
    }
}
