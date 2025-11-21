package com.uniclub.global.swagger;

import com.uniclub.domain.notification.dto.NotificationPageResponseDto;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림 API", description = "사용자 알림 조회, 읽음 처리, 삭제 기능")
public interface NotificationApiSpecification {

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 페이징하여 조회. 읽음 여부로 필터링 가능")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "notifications": [
                        {
                          "notificationId": 1,
                          "title": "앱센터 모집 시작",
                          "message": "질문에 답변이 도착했어요.",
                          "read": false,
                          "notificationType": "PERSONAL",
                          "targetId": 123,
                          "createdAt": "2025-08-03T14:30:00"
                        },
                        {
                          "notificationId": 2,
                          "title": "디자인소모임 홍보",
                          "message": "새로운 홍보글이 등록되었습니다.",
                          "read": true,
                          "notificationType": "BROADCAST",
                          "targetId": 456,
                          "createdAt": "2025-08-02T10:15:00"
                        }
                      ],
                      "currentPage": 0,
                      "totalPages": 5,
                      "totalElements": 47,
                      "hasNext": true
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "USER_NOT_FOUND",
                      "message": "해당 유저를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<NotificationPageResponseDto> getNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Boolean isRead
    );

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "읽음 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "NOTIFICATION_NOT_FOUND",
                      "message": "해당 알림을 찾을 수 없습니다."
                    }
                    """
                            )
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
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    );

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 읽지 않은 알림을 읽음 상태로 변경")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "모든 알림 읽음 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "USER_NOT_FOUND",
                      "message": "해당 유저를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> markAsReadAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "NOTIFICATION_NOT_FOUND",
                      "message": "해당 알림을 찾을 수 없습니다."
                    }
                    """
                            )
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
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    );

    @Operation(summary = "모든 알림 삭제", description = "사용자의 모든 알림을 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "모든 알림 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "USER_NOT_FOUND",
                      "message": "해당 유저를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteAllReadNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
