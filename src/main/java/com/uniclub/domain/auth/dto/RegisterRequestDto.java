package com.uniclub.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    // 직접 입력받는 방식이면 NotBlank 필요, 선택 방식이면 필요 X
    // @NotBlank(message = "전공을 입력해주세요.")
    private String major;

    private boolean agreed;
}
