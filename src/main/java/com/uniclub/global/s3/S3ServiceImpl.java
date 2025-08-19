package com.uniclub.global.s3;

import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Presigner s3Presigner;
    private final MembershipRepository membershipRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    //동아리 S3 presigned url 요청
    public List<S3PresignedResponseDto> getClubPresignedUrl(UserDetailsImpl userDetails, Long clubId, List<S3PresignedRequestDto> s3PresignedRequestDtoList) {
        log.info("동아리 presigned Url 요청: 학번={}, clubId={}", userDetails.getStudentId(), clubId);

        //해당 동아리의 운영진인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT && userRole != Role.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        List<S3PresignedResponseDto> s3PresignedResponseDtoList = new ArrayList<>();
        for (S3PresignedRequestDto s3PresignedRequestDto : s3PresignedRequestDtoList) {
            String filename = s3PresignedRequestDto.getFilename();
            String presignedUrl = getUploadPresignedUrl(filename);
            s3PresignedResponseDtoList.add(S3PresignedResponseDto.from(filename, presignedUrl));
        }
        log.info("동아리 presigned Url 발급");

        return s3PresignedResponseDtoList;
    }


    //메인페이지 S3 presigned url 요청
    public List<S3PresignedResponseDto> getMainPresignedUrl(List<S3PresignedRequestDto> s3PresignedRequestDtoList) {
        log.info("메인 페이지 presigned Url 요청");
        List<S3PresignedResponseDto> s3PresignedResponseDtoList = new ArrayList<>();
        for (S3PresignedRequestDto s3PresignedRequestDto : s3PresignedRequestDtoList) {
            String filename = s3PresignedRequestDto.getFilename();
            String presignedUrl = getUploadPresignedUrl(filename);
            s3PresignedResponseDtoList.add(S3PresignedResponseDto.from(filename, presignedUrl));
        }

        log.info("메인 페이지 presigned Url 발급");
        return s3PresignedResponseDtoList;
    }


    //단일 업로드
    private String getUploadPresignedUrl(String key) {
        // 파일명에서 확장자 추출
        String fileExtension = extractFileExtension(key);

        // 파일 확장자 검증
        validateFileExtension(fileExtension);

        String uniqueKey = generateUniqueKey(key);

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                req -> req.signatureDuration(Duration.ofMinutes(20))
                        .putObjectRequest(
                                PutObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(uniqueKey)
                                        .build()
                        )
        );
        return presignedPutObjectRequest.url().toString();
    }


    //조회용 presigned URL 생성
    @Override
    public String getDownloadPresignedUrl(String s3Key) {
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                req -> req.signatureDuration(Duration.ofHours(1)) // 1시간
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(s3Key)
                                        .build()
                        )
        );
        return presignedGetObjectRequest.url().toString();
    }


    //고유 url설정
    private String generateUniqueKey(String key) {
        String uuid = UUID.randomUUID().toString();
        String extension = "";
        int dotIndex = key.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = key.substring(dotIndex);
        }

        return String.format("uploads/%s/%s%s", LocalDate.now(), uuid, extension);
    }


    //파일명에서 확장자 추출
    private String extractFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        int lastDotIndex = filename.lastIndexOf(".");

        //확장자가 없거나, 파일명이 점으로 시작하는 경우
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        //파일명이 점으로 끝나는 경우
        if (lastDotIndex == filename.length() - 1) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        //확장자만 반환
        return filename.substring(lastDotIndex + 1);
    }


    //파일 확장자 검증
    private boolean validateFileExtension(String fileExtension) {
        // 허용 가능한 파일 확장자 목록
        Set<String> allowedExtensions = Set.of(
                // 이미지
                "jpg", "jpeg", "png", "gif", "webp", "svg",
                // 비디오
                "mp4", "avi", "mov", "wmv", "flv", "webm"
        );

        // 허용된 확장자인지 확인
        boolean isAllowed = allowedExtensions.contains(fileExtension.toLowerCase());

        if (!isAllowed) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        return true;
    }


    //특정 동아리 유저 권한 확인 (정보 없으면 GUEST로 반환)
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        return membershipRepository.findByUserIdAndClubId(userId, clubId)
                .map(MemberShip::getRole)
                .orElse(Role.GUEST);
    }
}
