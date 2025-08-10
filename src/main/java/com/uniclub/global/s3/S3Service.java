package com.uniclub.global.s3;

import com.uniclub.domain.club.entity.*;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    private final S3Client s3Client;
    private final MediaRepository mediaRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    //s3에 동아리 파일 업로드
    public List<MediaUploadResponseDto> uploadClubMedia(UserDetailsImpl userDetails, Long clubId, MediaUploadRequestDto mediaUploadRequestDto) {
        //해당 동아리의 운영진인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT && userRole != Role.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //유효성 검사
        validateUploadRequest(mediaUploadRequestDto);

        //동아리 존재 여부 확인
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 메인 이미지 처리 (기존 메인 이미지 해제)
        mediaRepository.findByClubAndIsMainTrue(club);

        List<MediaUploadResponseDto> mediaUploadResponseDtoList = new ArrayList<>();

        for (int i = 0; i < mediaUploadRequestDto.getMultipartFileList().size(); i++) {
            MultipartFile multipartFile = mediaUploadRequestDto.getMultipartFileList().get(i);
            MediaType mediaType = mediaUploadRequestDto.getMediaTypes().get(i);
            Boolean isMain = mediaUploadRequestDto.getIsMainList().get(i);

            //S3 업로드
            String mediaLink = uploadMediaToS3(multipartFile);

            //Entity로 변환 후 저장
            Media media = mediaUploadRequestDto.toMediaEntity(mediaLink, mediaType, isMain);
            mediaRepository.save(media);

            //반환 리스트에 저장
            mediaUploadResponseDtoList.add(MediaUploadResponseDto.from(media));
        }
        return mediaUploadResponseDtoList;

    }


    //s3에 메인 페이지 파일 업로드
    public void uploadMainMedia(MediaUploadRequestDto mediaUploadRequestDto) {

    }

    //s3에 파일 업로드
    private String uploadMediaToS3(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        //파일 이름 중복 없도록
        String uniqueFileName = uniqueFileName(extension);

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            return "https://" + bucketName + ".s3.amazonaws.com/" + uniqueFileName;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

    }

    //파일 확장자 추출
    private String getFileExtension(String fileName) {
        /*
            파일 확장자 예외 처리
         */

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    //파일 URL 만들기
    private String uniqueFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("club/%s_%s.%s", timestamp, uuid, extension);
    }

    //파일 유효성 검증
    private void validateUploadRequest(MediaUploadRequestDto mediaUploadRequestDto) {
        //메개변수 값이 제대로 입력되었는지 확인
        int fileCount = mediaUploadRequestDto.getMultipartFileList().size();
        if (mediaUploadRequestDto.getMediaTypes().size() != fileCount ||
                mediaUploadRequestDto.getIsMainList().size() != fileCount) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 파일 개수 제한
        if (fileCount > 10) {
            throw new CustomException(ErrorCode.FILE_COUNT_EXCEEDED);
        }
    }

    //특정 동아리 유저 권한 확인
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        MemberShip memberShip = membershipRepository.findByUserIdAndClubId(userId, clubId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
        return memberShip.getRole();
    }
}
