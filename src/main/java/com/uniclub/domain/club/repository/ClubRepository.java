package com.uniclub.domain.club.repository;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    @Query("SELECT c FROM Club c JOIN c.category ca WHERE ca.name = :categoryName")
    List<Club> findByCategoryName(CategoryType categoryName);
    Boolean existsByName(String name);

    @Query("SELECT c FROM Club c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Club> findByKeyword(String keyword);
}
