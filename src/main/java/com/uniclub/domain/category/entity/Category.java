package com.uniclub.domain.category.entity;

import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId; // PK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType name; // 카테고리명

    public Category(CategoryType name) {
        this.name = name;
    }
}
