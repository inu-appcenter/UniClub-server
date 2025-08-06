package com.uniclub.domain.category.controller;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.service.CategoryService;
import com.uniclub.global.swagger.CategoryApiSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController implements CategoryApiSpecification {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
