package com.uniclub.domain.favorite.repository;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("SELECT f.club.clubId FROM Favorite f WHERE f.user.userId = :userId")
    List<Long> findClubIdsByUserId(Long userId);

    boolean existsByUserAndClub(User user, Club club);
    void deleteByUserAndClub(User user, Club club);


}
