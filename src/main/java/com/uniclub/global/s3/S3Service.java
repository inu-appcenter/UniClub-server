package com.uniclub.global.s3;

import com.uniclub.global.exception.CustomException;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    //s3에 동아리 파일 업로드
    public List<MediaUploadResponseDto> uploadClubMedia(String clubId, MediaUploadRequestDto mediaUploadRequestDto) {

    }


    //s3에 메인 페이지 파일 업로드
    public void uploadMainMedia(MediaUploadRequestDto mediaUploadRequestDto) {

    }

    private String uploadMediaToS3(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        //파일 이름 중복 없도록
        String uniqueFileName = uniqueFileName(originalFileName, extension);

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            return "https://" + bucketName + ".s3.amazonaws.com/" + uniqueFileName;
        } catch (Exception e) {
            throw new CustomException(null);    //예외처리
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
    private String uniqueFileName(String extension, String clubId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("club/%s/%s_%s.%s", clubId, timestamp, uuid, extension);
    }
}
