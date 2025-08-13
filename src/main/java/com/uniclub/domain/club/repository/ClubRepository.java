package com.uniclub.domain.club.repository;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 이름순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY c.name ASC")
    Slice<Club> findClubsByCursorOrderByName(@Param("categoryName") CategoryType categoryName,
                                             @Param("cursorName") String cursorName,
                                             Pageable pageable);

    // 좋아요순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "LEFT JOIN Favorite f ON f.club = c AND f.user.userId = :userId " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY CASE WHEN f.favoriteId IS NOT NULL THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByFavorite(@Param("userId") Long userId,
                                                 @Param("categoryName") CategoryType categoryName,
                                                 @Param("cursorName") String cursorName,
                                                 Pageable pageable);

    // 모집중순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "ORDER BY CASE WHEN c.status = 'ACTIVE' THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByStatus(@Param("categoryName") CategoryType categoryName,
                                               @Param("cursorName") String cursorName,
                                               Pageable pageable);



    @Query("""
    select new com.uniclub.domain.main.dto.MainPageClubResponseDto(
        c.name,
        m.mediaLink,
        case when f.favoriteId is not null then true else false end
    )
    from Club c
    join Media m on m.club = c and m.isMain = true
    left join Favorite f on f.club = c and f.user.userId = :userId
    order by function('rand')
    """)
    List<MainPageClubResponseDto> getMainPageClubs(Pageable pageable);
}
