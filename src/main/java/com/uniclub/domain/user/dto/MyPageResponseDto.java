package com.uniclub.domain.user.dto;

import com.uniclub.domain.user.entity.Major;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "마이페이지 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class MyPageResponseDto {

    @Schema(description = "닉네임", example = "라면")
    private final String nickname;

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "학번", example = "23학번")
    private final String studentId;

    @Schema(description = "전공", example = "COMPUTER_ENGINEERING")
    private final Major major;

    @Schema(description = "프로필 이미지 URL", example = "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4")
    private final String profileImageLink;
}