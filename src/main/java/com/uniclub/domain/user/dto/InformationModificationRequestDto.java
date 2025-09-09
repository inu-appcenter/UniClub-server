package com.uniclub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "유저 개인정보 수정 요청 DTO")
@Getter
public class InformationModificationRequestDto {

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "전공", example = "컴퓨터공학부")
    private String major;

    @Schema(description = "닉네임", example = "빌려온 고양이")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png")
    private String profileImageLink;
}
