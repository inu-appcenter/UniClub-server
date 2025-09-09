package com.uniclub.global.swagger;

import com.uniclub.domain.notification.dto.NotificationRequestDto;
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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "알림 API", description = "사용자 알림 조회 기능")
public interface NotificationApiSpecification {

    @Operation(summary = "알림 리스트 조회", description = "사용자 알림 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationResponseDto.class),
                            examples = @ExampleObject("""
                    [
                      {
                        "notificationId": 1,
                        "message": "질문에 답변이 도착했어요.",
                        "type": "PERSONAL",
                        "createdAt": "2025-08-03T14:30:00",
                        "read": false
                      },
                      {
                        "notificationId": 2,
                        "message": "동아리 가입 요청이 승인되었습니다.",
                        "type": "SYSTEM",
                        "createdAt": "2025-08-02T09:15:30",
                        "read": true
                      }
                    ]
                    """
                            )
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
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<NotificationResponseDto>> getNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );


    @Operation(summary = "알림 생성(테스트용)", description = "알림 생성 (테스트용)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림 타입 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "NOTIFICATION_TYPE_NOT_FOUND",
                      "message": "해당 알림 타입을 찾을 수 없습니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<Void> registerNotification(@Valid @RequestBody NotificationRequestDto notificationRequestDto);

    @Operation(summary = "알림 삭제", description = "사용자의 특정 알림 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "NOTIFICATION_NOT_FOUND",
                      "message": "해당 알림을 찾을 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
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
            )
    })
    ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    );

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "NOTIFICATION_NOT_FOUND",
                      "message": "해당 알림을 찾을 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
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
            )
    })
    ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    );
}
