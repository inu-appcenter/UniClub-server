package com.uniclub.domain.club.repository;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Query("SELECT m FROM Media m WHERE m.club.clubId = :clubId")
    List<Media> findByClubId(Long clubId);

    @Modifying
    @Query("UPDATE Media m SET m.isMain = false WHERE m.club = :club AND m.isMain = true")
    List<Media> findByClubAndIsMainTrue(Club club);
}
