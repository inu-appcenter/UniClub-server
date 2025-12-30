package com.uniclub.global.swagger;

import com.uniclub.domain.fcm.dto.FcmRegisterRequestDto;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "FCM API", description = "Firebase Cloud Messaging 토큰 관리 기능")
public interface FcmApiSpecification {

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록하여 푸시 알림 수신 설정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "INVALID_INPUT_VALUE",
                      "message": "FCM 토큰은 필수입니다."
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
    ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FcmRegisterRequestDto fcmRegisterRequestDto
    );

    @Operation(summary = "FCM 토큰 삭제", description = "사용자의 FCM 토큰을 삭제하여 푸시 알림 수신 중단")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 삭제 성공"
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "FCM 토큰 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "FCM_TOKEN_NOT_FOUND",
                      "message": "FCM 토큰을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
