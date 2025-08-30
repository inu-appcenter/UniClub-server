package com.uniclub.domain.terms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "개인정보 약관 동의 정보 저장 요청 DTO")
@Getter
public class RegisterTermsRequestDto {

    @Schema(description = "학번", example = "202012345")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "개인정보 약관 동의", example = "true")
    @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
    private boolean personalInfoCollectionAgreement;

    @Schema(description = "마케팅 및 광고 활용 동의", example = "false")
    @NotNull(message = "마케팅 및 광고 활용 동의 여부를 입력하세요.")
    private boolean marketingAdvertisement;
}
