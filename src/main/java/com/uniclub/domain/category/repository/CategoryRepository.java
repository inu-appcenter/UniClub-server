package com.uniclub.domain.category.repository;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(CategoryType name);

    Optional<Category> findByName(CategoryType name);
}
