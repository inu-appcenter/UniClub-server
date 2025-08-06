package com.uniclub.global.s3;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MediaUploadResponseDto {
    private Long mediaId;
    private String mediaLink;
    private MediaType mediaType;
    private Boolean isMain;

    @Builder
    public MediaUploadResponseDto(String mediaLink, MediaType mediaType, Boolean isMain) {
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.isMain = isMain;
    }

    public static MediaUploadResponseDto from(Media media) {
        return MediaUploadResponseDto.builder()
                .mediaLink(media.getMediaLink())
                .mediaType(media.getMediaType())
                .isMain(media.getIsMain())
                .build();
    }
}
