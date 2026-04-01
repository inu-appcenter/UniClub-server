package com.uniclub.domain.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "동아리 멤버 역할 변경 요청 DTO")
@Getter
public class MemberRoleChangeRequestDto {

    @Schema(description = "대상 사용자 학번", example = "202101234")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "동아리 ID", example = "1")
    @NotNull(message = "동아리 ID를 입력해주세요.")
    private Long clubId;

    @Schema(description = "부여할 역할 (MEMBER, ADMIN, PRESIDENT)", example = "PRESIDENT")
    @NotBlank(message = "역할을 입력해주세요.")
    private String role;
}
