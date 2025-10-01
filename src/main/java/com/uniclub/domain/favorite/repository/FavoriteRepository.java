package com.uniclub.domain.favorite.repository;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("SELECT f.club.clubId FROM Favorite f WHERE f.user.userId = :userId")
    List<Long> findClubIdsByUserId(Long userId);

    @Query("SELECT f.user.userId FROM Favorite f WHERE f.club.clubId = :clubId")
    List<Long> findUserIdsByClubId(Long clubId);

    @Query("""
    SELECT CASE WHEN EXISTS (
        SELECT 1 FROM Favorite f
        WHERE f.user.userId = :userId AND f.club.clubId = :clubId
    ) THEN true ELSE false END
    """)
    boolean existsByUserIdAndClubId(@Param("userId") Long userId, @Param("clubId") Long clubId);

    void deleteByUserAndClub(User user, Club club);
}
