package com.uniclub.global.swagger;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "카테고리 API")
public interface CategoryApiSpecification {
    @Operation(summary = "카테고리 생성")
    public ResponseEntity<Long> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto);

    @Operation(summary = "카테고리 삭제")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId);
}
