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
}
