package com.uniclub.domain.category.controller;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> createCategory(@RequestBody CategoryRequestDto request) {
        Long id = categoryService.createCategory(request);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
