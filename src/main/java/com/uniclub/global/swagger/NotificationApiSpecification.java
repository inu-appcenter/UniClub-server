package com.uniclub.global.swagger;

import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.global.exception.ErrorResponse;
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

import java.util.List;

@Tag(name = "알림 API", description = "사용자 알림 조회 기능")
public interface NotificationApiSpecification {

    @Operation(summary = "알림 리스트 조회", description = "인증된 사용자의 알림 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationResponseDto.class),
                            examples = @ExampleObject("""
                    [
                      {
                        "message": "질문에 답변이 도착했어요.",
                        "isRead": false,
                        "type": "PERSONAL",
                        "createdAt": "2025-08-03T14:30:00"
                      },
                      {
                        "message": "동아리 가입 요청이 승인되었습니다.",
                        "isRead": true,
                        "type": "SYSTEM",
                        "createdAt": "2025-08-02T09:15:30"
                      }
                    ]
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403", description = "사용 권한이 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 403,
                      "name": "INSUFFICIENT_PERMISSION",
                      "message": "사용 권한이 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<NotificationResponseDto>> getNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
