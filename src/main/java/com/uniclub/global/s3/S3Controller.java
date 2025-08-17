package com.uniclub.global.s3;

import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.S3ApiSpecification;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class S3Controller implements S3ApiSpecification {

    private final S3Service s3Service;
    private final S3ServiceImpl s3ServiceImpl;

    //동아리 홍보글 미디어 S3 presigned url 요청
    @PostMapping("/club/{clubId}/s3-presigned")
    @Operation(summary = "동아리 S3 presigned url", description = "동아리 미디어 S3 presigned url 요청")
    public ResponseEntity<List<S3PresignedResponseDto>> getClubS3Presigned(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId, @RequestBody List<S3PresignedRequestDto> s3PresignedRequestDtoList) {
        List<S3PresignedResponseDto> s3PresignedResponseDtoList = s3ServiceImpl.getClubPresignedUrl(userDetails, clubId, s3PresignedRequestDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(s3PresignedResponseDtoList);
    }

    //메인 페이지 미디어 S3 presigned url 요청
    @PostMapping("/main/s3-presigned")
    @Operation(summary = "메인 페이지 S3 presigned url", description = "메인 페이지 미디어 S3 presigned url 요청")
    public ResponseEntity<List<S3PresignedResponseDto>> getMainS3Presigned(@RequestBody List<S3PresignedRequestDto> s3PresignedRequestDtoList) {
        List<S3PresignedResponseDto> s3PresignedResponseDtoList = s3ServiceImpl.getMainPresignedUrl(s3PresignedRequestDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(s3PresignedResponseDtoList);
    }
}