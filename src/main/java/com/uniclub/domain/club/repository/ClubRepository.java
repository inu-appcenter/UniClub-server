package com.uniclub.domain.club.repository;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.qna.dto.QnaClubResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Boolean existsByName(String name);

    Optional<Club> findByName(String name);

    @Query("SELECT c FROM Club c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND c.deleted = false")
    List<Club> findByKeyword(String keyword);

    // 이름순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "AND c.deleted = false " +
            "ORDER BY c.name ASC")
    Slice<Club> findClubsByCursorOrderByName(@Param("categoryName") CategoryType categoryName,
                                             @Param("cursorName") String cursorName,
                                             Pageable pageable);

    // 좋아요순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "LEFT JOIN Favorite f ON f.club = c AND f.user.userId = :userId " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "AND c.deleted = false " +
            "ORDER BY CASE WHEN f.favoriteId IS NOT NULL THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByFavorite(@Param("userId") Long userId,
                                                 @Param("categoryName") CategoryType categoryName,
                                                 @Param("cursorName") String cursorName,
                                                 Pageable pageable);

    // 모집중순 + 카테고리
    @Query("SELECT c FROM Club c " +
            "WHERE (:categoryName IS NULL OR c.category.name = :categoryName) " +
            "AND (:cursorName IS NULL OR c.name > :cursorName) " +
            "AND c.deleted = false " +
            "ORDER BY CASE WHEN c.status = 'ACTIVE' THEN 0 ELSE 1 END, c.name ASC")
    Slice<Club> findClubsByCursorOrderByStatus(@Param("categoryName") CategoryType categoryName,
                                               @Param("cursorName") String cursorName,
                                               Pageable pageable);


    //전체 조회(키워드 공백시)
    @Query("SELECT new com.uniclub.domain.qna.dto.QnaClubResponseDto(c.clubId, c.name, cat.name) " +
            "FROM Club c " +
            "INNER JOIN c.category cat " +
            "ORDER BY c.name")
    List<QnaClubResponseDto> searchAllClubsForQna();

    @Query("SELECT new com.uniclub.domain.qna.dto.QnaClubResponseDto(c.clubId, c.name, cat.name) " +
            "FROM Club c " +
            "INNER JOIN c.category cat " +
            "WHERE c.name LIKE %:keyword% " +
            "ORDER BY c.name")
    List<QnaClubResponseDto> searchClubsForQna(@Param("keyword") String keyword);



    @Query("""
    SELECT NEW com.uniclub.domain.main.dto.MainPageClubResponseDto(c.clubId, c.name, m.mediaLink, CASE WHEN f.favoriteId IS NOT NULL THEN true ELSE false END)
    FROM Club c
    LEFT JOIN Media m ON m.club = c AND m.mainMedia = true
    LEFT JOIN Favorite f ON f.club = c AND f.user.userId = :userId
    WHERE c.deleted = false
    ORDER BY RAND()
    """)
    List<MainPageClubResponseDto> getMainPageClubs(@Param("userId") Long userId, Pageable pageable);

    String findNameByClubId(Long clubId);


    //알림 스케줄링 관련

    //24시간 이내 모집 시작된 동아리
    @Query("SELECT c FROM Club c WHERE c.startTime >= CURRENT_TIMESTAMP - 1 DAY AND c.startTime < CURRENT_TIMESTAMP AND c.status = 'ACTIVE'")
    List<Club> findRecruitmentStartedInLast24Hours();

    //n일 후 모집 마감되는 동아리
    @Query("SELECT c FROM Club c WHERE DATE(c.endTime) = DATE(CURRENT_DATE + :days) AND c.status = 'ACTIVE'")
    List<Club> findRecruitmentEndingInDays(@Param("days") int days);
}
