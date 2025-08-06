package com.uniclub.global.swagger;

import com.uniclub.domain.auth.dto.LoginRequestDto;
import com.uniclub.domain.auth.dto.LoginResponseDto;
import com.uniclub.domain.auth.dto.RegisterRequestDto;
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

    @Operation(summary = "회원가입", description = "새 사용자 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(
                    responseCode = "400", description = "개인정보 약관에 동의하지 않음",
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
                    responseCode = "409", description = "이미 존재하는 계정",
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


    @Operation(summary = "로그인", description = "가입된 사용자로 로그인하고 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204", description = "로그인 성공",
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
}
