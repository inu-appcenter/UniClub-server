package com.uniclub.global.s3;

import com.uniclub.global.security.UserDetailsImpl;

import java.util.List;

public interface S3Service {
    //동아리 S3 presigned url 요청
    List<S3PresignedResponseDto> getClubPresignedUrl(UserDetailsImpl userDetails, Long clubId, List<S3PresignedRequestDto> s3PresignedRequestDtoList);

    //메인페이지 S3 presigned url 요청
    List<S3PresignedResponseDto> getMainPresignedUrl(List<S3PresignedRequestDto> s3PresignedRequestDtoList);

    //프로필 이미지 S3 presigned url 요청
    S3PresignedResponseDto getUserProfilePresignedUrl(UserDetailsImpl userDetails, S3PresignedRequestDto s3PresignedRequestDto);

    String getDownloadPresignedUrl(String s3Key);

    void deleteFile(String s3Key);

    void deleteFiles(List<String> s3Keys);
}
