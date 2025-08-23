package com.uniclub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "마이페이지 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class MyPageResponseDto {

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "학번", example = "23학번")
    private final String studentId;

    @Schema(description = "전공", example = "컴퓨터공학부")
    private final String major;
}