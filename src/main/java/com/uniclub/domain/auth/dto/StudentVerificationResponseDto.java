package com.uniclub.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;

@Schema(description = "재학생 인증 응답 DTO")
@Getter
public class StudentVerificationResponseDto {

    @Schema(description = "재학생 여부", example = "true")
    private final boolean verification;

    public StudentVerificationResponseDto(boolean verification) {
        this.verification = verification;
    }
}
