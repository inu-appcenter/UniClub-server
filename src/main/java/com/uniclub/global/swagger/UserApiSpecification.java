package com.uniclub.global.swagger;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.dto.MyPageResponseDto;
import com.uniclub.domain.user.dto.NotificationSettingResponseDto;
import com.uniclub.domain.user.dto.ToggleNotificationResponseDto;
import com.uniclub.domain.user.dto.UserDeleteRequestDto;
import com.uniclub.domain.user.dto.UserRoleRequestDto;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 API", description = "유저 정보 조회, 수정, 탈퇴, 알림설정 기능")
public interface UserApiSpecification {

    @Operation(summary = "마이페이지 조회", description = "로그인한 유저의 이름, 학번, 전공 정보 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MyPageResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "name": "홍길동",
                      "studentId": "20학번",
                      "major": "컴퓨터공학부"
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
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "삭제된 유저",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 410,
                      "name": "USER_DELETED",
                      "message": "삭제된 유저입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<MyPageResponseDto> getMyPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "유저 개인정보 수정", description = "유저 이름 및 전공 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "수정 성공"
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
                    responseCode = "410",
                    description = "삭제된 유저",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 410,
                      "name": "USER_DELETED",
                      "message": "삭제된 유저입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto informationModificationRequestDto
    );

    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "탈퇴 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "비밀번호가 일치하지 않음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 401,
                      "name": "PASSWORD_NOT_MATCHED",
                      "message": "비밀번호가 일치하지 않습니다."
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
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "이미 삭제된 유저",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 410,
                      "name": "USER_DELETED",
                      "message": "삭제된 유저입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserDeleteRequestDto userDeleteRequestDto
    );

    @Operation(summary = "유저 권한 부여", description = "테스트용 - 유저에게 동아리 권한 부여")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "권한 부여 성공"
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
                    description = "동아리 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CLUB_NOT_FOUND",
                      "message": "해당 동아리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "권한 타입 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "ROLE_NOT_FOUND",
                      "message": "해당 권한을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> addRole(
            @RequestBody UserRoleRequestDto userRoleRequestDto
    );

    @Operation(summary = "알림설정 조회", description = "로그인한 유저의 알림 활성화 여부 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationSettingResponseDto.class))
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
                    responseCode = "410",
                    description = "삭제된 유저",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 410,
                      "name": "USER_DELETED",
                      "message": "삭제된 유저입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<NotificationSettingResponseDto> getNotificationSetting(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "알림설정 토글", description = "로그인한 유저의 알림 설정을 on/off 토글")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토글 성공",
                    content = @Content(
                            schema = @Schema(implementation = ToggleNotificationResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "알림 활성화",
                                            description = "알림이 비활성화 -> 활성화로 변경된 경우",
                                            value = """
                                    {
                                      "message": "알림이 활성화되었습니다."
                                    }
                                    """
                                    ),
                                    @ExampleObject(
                                            name = "알림 비활성화",
                                            description = "알림이 활성화 -> 비활성화로 변경된 경우",
                                            value = """
                                    {
                                      "message": "알림이 비활성화되었습니다."
                                    }
                                    """
                                    )
                            }
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
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "삭제된 유저",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 410,
                      "name": "USER_DELETED",
                      "message": "삭제된 유저입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<ToggleNotificationResponseDto> toggleNotificationSetting(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}