package com.uniclub.global.s3;

public interface S3Service {
    public String getDownloadPresignedUrl(String s3Key);
}
