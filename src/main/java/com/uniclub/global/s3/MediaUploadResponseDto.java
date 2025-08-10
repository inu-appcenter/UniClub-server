package com.uniclub.global.s3;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MediaUploadResponseDto {
    private final Long mediaId;
    private final String mediaLink;
    private final MediaType mediaType;
    private final Boolean isMain;

    @Builder
    public MediaUploadResponseDto(Long mediaId, String mediaLink, MediaType mediaType, Boolean isMain) {
        this.mediaId = mediaId;
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
