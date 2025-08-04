package com.uniclub.domain.club.repository;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.entity.Club;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Boolean existsByName(String name);

    @Query("SELECT c FROM Club c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Club> findByKeyword(String keyword);

    // 이름순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:category IS NULL OR c.category = :category) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY c.name ASC")
    Slice<Club> findClubsByCursorOrderByName(@Param("category") CategoryType category,
                                             @Param("cursorName") String cursorName,
                                             Pageable pageable);

    // 좋아요순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "LEFT JOIN Favorite f ON f.club = c AND f.user.userId = :userId " +
            "WHERE (:category IS NULL OR c.category = :category) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY CASE WHEN f.favoriteId IS NOT NULL THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByFavorite(@Param("userId") Long userId,
                                                 @Param("category") CategoryType category,
                                                 @Param("cursorName") String cursorName,
                                                 Pageable pageable);

    // 모집중순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:category IS NULL OR c.category = :category) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY CASE WHEN c.status = 'ACTIVE' THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByStatus(@Param("category") CategoryType category,
                                               @Param("cursorName") String cursorName,
                                               Pageable pageable);

}
