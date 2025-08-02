package com.uniclub.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
