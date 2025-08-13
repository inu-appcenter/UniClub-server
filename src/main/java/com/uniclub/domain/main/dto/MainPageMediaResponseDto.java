package com.uniclub.domain.main.dto;

import com.uniclub.domain.club.dto.DescriptionMediaDto;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainPageMediaResponseDto {
    private final String mediaLink;
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
