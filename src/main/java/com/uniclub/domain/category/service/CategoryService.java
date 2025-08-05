package com.uniclub.domain.category.service;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.category.repository.CategoryRepository;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Long createCategory(CategoryRequestDto categoryRequestDto) {
        CategoryType categoryName = CategoryType.from(categoryRequestDto.getName());

        if (categoryRepository.existsByName(categoryName)) { // 카테고리 중복 확인
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }
        Category saved = categoryRepository.save(new Category(categoryName));
        return saved.getCategoryId();
    }


    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) { // 있는 카테고리인지 확인
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(categoryId);
    }
}
