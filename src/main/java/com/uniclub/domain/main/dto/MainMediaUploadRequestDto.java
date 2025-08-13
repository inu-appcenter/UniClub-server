package com.uniclub.domain.main.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "메인 페이지 미디어 업로드 요청 DTO")
@Getter
@NoArgsConstructor
public class MainMediaUploadRequestDto {
    private String mediaLink;
    private MediaType mediaType;

    public Media toMediaEntity() {
        return Media.builder()
                .mediaLink(mediaLink)
                .mediaType(mediaType)
                .build();
    }
}
