package com.uniclub.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "카테고리 생성 요청 DTO")
@Getter
public class CategoryRequestDto {
    @Schema(description = "카테고리 이름", example = "학술")
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String name;
}
