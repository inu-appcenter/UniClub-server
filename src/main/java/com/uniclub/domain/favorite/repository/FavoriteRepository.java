package com.uniclub.domain.favorite.repository;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndClub(User user, Club club);
    void deleteByUserAndClub(User user, Club club);
}
