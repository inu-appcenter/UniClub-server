package com.uniclub.global.s3;

import com.uniclub.global.security.UserDetailsImpl;
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
public class S3Controller {

    private final S3Service s3Service;

    //동아리 홍보글 관련 Media file
    @PostMapping("/club/{clubId}/upload")
    @Operation(summary = "동아리 파일 업로드", description = "특정 동아리 파일을 업로드")
    public ResponseEntity<List<MediaUploadResponseDto>> uploadClubMedia(@PathVariable Long clubId, @AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute MediaUploadRequestDto mediaUploadRequestDto) {
        List<MediaUploadResponseDto> mediaUploadResponseDtoList = s3Service.uploadClubMedia(userDetails, clubId, mediaUploadRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaUploadResponseDtoList);
    }

    //메인 페이지 Media file
    @PostMapping("/main/upload")
    @Operation(summary = "메인 페이지 파일 업로드", description = "메인 페이지 파일을 업로드")
    public ResponseEntity<Void> uploadMainMedia(@ModelAttribute MediaUploadRequestDto mediaUploadRequestDto) {
        s3Service.uploadMainMedia(mediaUploadRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
