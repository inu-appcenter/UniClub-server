package com.uniclub.global.s3;

import lombok.Builder;
import lombok.Getter;

@Getter
public class S3PresignedResponseDto {
    private final String filename;
    private final String presignedUrl;

    @Builder
    public S3PresignedResponseDto(String filename, String presignedUrl) {
        this.filename = filename;
        this.presignedUrl = presignedUrl;
    }

    public static S3PresignedResponseDto from(String filename, String presignedUrl) {
        return new S3PresignedResponseDto(filename, presignedUrl);
    }
}
