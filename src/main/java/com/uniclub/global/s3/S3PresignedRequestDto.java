package com.uniclub.global.s3;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3PresignedRequestDto {
    private String filename;
}
