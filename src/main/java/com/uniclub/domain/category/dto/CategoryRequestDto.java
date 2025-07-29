package com.uniclub.domain.category.dto;

import com.uniclub.domain.category.entity.CategoryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequestDto {
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private CategoryType name;
}
