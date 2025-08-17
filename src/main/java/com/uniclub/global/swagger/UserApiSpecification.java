package com.uniclub.global.swagger;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
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

@Tag(name = "유저 API", description = "유저 정보 수정·탈퇴 기능")
public interface UserApiSpecification {

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
            )
    })
    ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto informationModificationRequestDto
    );

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "탈퇴 성공"
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
    ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
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
}