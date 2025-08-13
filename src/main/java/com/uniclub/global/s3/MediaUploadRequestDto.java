package com.uniclub.global.s3;

import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class MediaUploadRequestDto {

    private List<MultipartFile> multipartFileList;
    private List<String> mediaTypes;
    private List<Boolean> isMainList;

    public Media toMediaEntity(String mediaLink, MediaType mediaType, Boolean isMain) {
        return Media.builder()
                .mediaLink(mediaLink)
                .mediaType(mediaType)
                .isMain(isMain)
                .build();
    }


}
