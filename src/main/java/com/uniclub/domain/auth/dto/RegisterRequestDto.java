package com.uniclub.domain.auth.dto;

import com.uniclub.domain.user.entity.Major;
import com.uniclub.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    @Schema(description = "학번", example = "202012345")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "사용자 전공")
    @NotBlank(message = "전공을 입력해주세요.")
    private String major;

    @Schema(description = "닉네임", example = "밥줘배고파")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Schema(description = "개인정보 약관 동의", example = "true")
    @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
    private boolean personalInfoCollectionAgreement;

    @Schema(description = "마케팅 및 광고 활용 동의", example = "false")
    @NotNull(message = "마케팅 및 광고 활용 동의 여부를 입력하세요.")
    private boolean marketingAdvertisement;

    @Schema(description = "재학생 인증 여부", example = "true")
    @NotNull(message = "재학생 인증을 진행하세요.")
    private boolean studentVerification;
}
