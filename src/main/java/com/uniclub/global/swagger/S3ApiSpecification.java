package com.uniclub.global.swagger;

import com.uniclub.global.exception.ErrorResponse;
import com.uniclub.global.s3.S3PresignedRequestDto;
import com.uniclub.global.s3.S3PresignedResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "S3 API", description = "AWS S3 Presigned URL 생성 기능")
public interface S3ApiSpecification {

    @Operation(summary = "동아리 S3 presigned url", description = "동아리 미디어 업로드를 위한 S3 Presigned URL 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Presigned URL 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = S3PresignedResponseDto.class),
                            examples = @ExampleObject("""
                    [
                      {
                        "filename": "club_poster.jpg",
                        "presignedUrl": "https://uniclub-bucket.s3.ap-northeast-2.amazonaws.com/uploads/2025-01-15/abc123-def456-ghi789.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
                      },
                      {
                        "filename": "activity_video.mp4",
                        "presignedUrl": "https://uniclub-bucket.s3.ap-northeast-2.amazonaws.com/uploads/2025-01-15/xyz789-uvw456-rst123.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
                      }
                    ]
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403", description = "권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 403,
                      "name": "INSUFFICIENT_PERMISSION",
                      "message": "사용 권한이 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", description = "멤버십 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "멤버십 없음", value = """
                            {
                              "code": 404,
                              "name": "MEMBERSHIP_NOT_FOUND",
                              "message": "해당 동아리 권한을 찾을 수 없습니다."
                            }
                            """)
                            }
                    )
            )
    })
    ResponseEntity<List<S3PresignedResponseDto>> getClubS3Presigned(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long clubId,
            @RequestBody List<S3PresignedRequestDto> s3PresignedRequestDtoList
    );

    @Operation(summary = "메인 페이지 S3 presigned url", description = "메인페이지 미디어 업로드를 위한 S3 Presigned URL 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Presigned URL 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = S3PresignedResponseDto.class),
                            examples = @ExampleObject("""
                    [
                      {
                        "filename": "main_banner.jpg",
                        "presignedUrl": "https://uniclub-bucket.s3.ap-northeast-2.amazonaws.com/uploads/2025-01-15/main-abc123-def456.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
                      },
                      {
                        "filename": "notice_image.png",
                        "presignedUrl": "https://uniclub-bucket.s3.ap-northeast-2.amazonaws.com/uploads/2025-01-15/main-xyz789-uvw456.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
                      }
                    ]
                    """)
                    )
            )
    })
    ResponseEntity<List<S3PresignedResponseDto>> getMainS3Presigned(
            @RequestBody List<S3PresignedRequestDto> s3PresignedRequestDtoList
    );
}