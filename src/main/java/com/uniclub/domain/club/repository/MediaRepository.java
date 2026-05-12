package com.uniclub.domain.club.repository;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Query("SELECT m FROM Media m WHERE m.club.clubId = :clubId AND m.deleted = false")
    List<Media> findByClubId(Long clubId);

    @Query("SELECT m FROM Media m WHERE m.mediaType = :mediaType AND m.deleted = false")
    List<Media> findByMediaType(MediaType mediaType);

    @Query("SELECT m FROM Media m JOIN FETCH m.club WHERE m.club.clubId IN :clubIds AND m.mediaType = 'CLUB_PROFILE' AND m.deleted = false")
    List<Media> findClubProfilesByClubIds(List<Long> clubIds);

    @Query("SELECT m FROM Media m WHERE m.club.clubId = :clubId AND m.mediaType = :mediaType AND m.deleted = false")
    List<Media> findByClubIdAndMediaType(@Param("clubId") Long clubId, @Param("mediaType") MediaType mediaType);

    @Query("SELECT m FROM Media m WHERE m.deleted = true AND m.deletedAt < :cutoffDate")
    List<Media> findDeletedMediaBeforeDate(@Param("cutoffDate") LocalDateTime cutoffDate);

}
