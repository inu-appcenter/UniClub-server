package com.uniclub.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    @Schema(description = "학번", example = "202012345")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "비밀번호", example = "qwer123")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "사용자 전공", example = "컴퓨터공학부")
    @NotBlank(message = "전공을 입력해주세요.")
    private String major;

    @Schema(description = "개인정보 약관 동의", example = "true")
    private boolean agreed;
}
