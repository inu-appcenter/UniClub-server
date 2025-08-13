package com.uniclub.global.s3;

import com.uniclub.domain.club.entity.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3PresignedRequestDto {
    private String filename;
}
