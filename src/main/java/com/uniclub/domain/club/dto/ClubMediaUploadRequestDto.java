package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "동아리 미디어 업로드 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubMediaUploadRequestDto {
    private String mediaLink;
    private MediaType mediaType;
    private boolean isMain;

    public Media toMediaEntity(Club club) {
        return Media.builder()
                .mediaLink(mediaLink)
                .mediaType(mediaType)
                .isMain(isMain)
                .club(club)
                .build();
    }
}
