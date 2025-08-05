package com.uniclub.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO")
@Getter
@NoArgsConstructor
public class LoginRequestDto {

    @Schema(description = "학번", example = "202012345")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "비밀번호", example = "qwer123!")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
