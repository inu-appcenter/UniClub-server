package com.uniclub.domain.category.service;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.repository.CategoryRepository;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createCategory(CategoryRequestDto request) {
        Category category = new Category(request.getName());
        Category saved = categoryRepository.save(category);
        return saved.getCategoryId();
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(categoryId);
    }
}
