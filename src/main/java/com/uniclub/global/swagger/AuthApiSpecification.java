package com.uniclub.global.swagger;

import com.uniclub.domain.auth.dto.*;
import com.uniclub.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증 API", description = "회원가입·로그인 관련 기능")
public interface AuthApiSpecification {

    @Operation(summary = "회원가입", description = "신규 회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(
                    responseCode = "400", description = "개인정보 약관 미동의",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                        "code": 400,
                        "name": "NOT_AGREED",
                        "message": "개인정보 약관에 동의해야 합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409", description = "계정 중복",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                        "code": 409,
                        "name": "DUPLICATE_STUDENT_ID",
                        "message": "이미 존재하는 계정입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> createUser(@Valid @RequestBody RegisterRequestDto request);


    @Operation(summary = "로그인", description = "로그인 및 토큰 발급")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            schema = @Schema(implementation = LoginResponseDto.class),
                            examples = @ExampleObject("""
                    {
                        "userId": 123,
                        "accessToken": "6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B",
                        "tokenType": "Bearer",
                        "expiresIn": 3600
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto);


    @Operation(summary = "재학생 인증", description = "학교 포털 재학생 인증")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "인증 결과 반환",
                    content = @Content(
                            schema = @Schema(implementation = StudentVerificationResponseDto.class),
                            examples = @ExampleObject("""
                    {
                        "verification": true
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "502", description = "학교 서버 통신 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                        "code": 502,
                        "name": "SCHOOL_SERVER_ERROR",
                        "message": "학교 서버 응답에 문제가 발생했습니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<StudentVerificationResponseDto> studentVerification(
            @Valid @RequestBody StudentVerificationRequestDto studentVerificationRequestDto
    );
}