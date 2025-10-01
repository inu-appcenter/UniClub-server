package com.uniclub.domain.fcm.repository;

import com.uniclub.domain.fcm.entity.FcmToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    @Query("SELECT ft.token FROM FcmToken ft WHERE ft.userId IN :userIds")
    List<String> findTokensByUserIds(List<Long> userIds);

    Optional<FcmToken> findByToken(String fcmToken);

    void deleteByToken(String token);

    List<FcmToken> userId(Long userId);

    @Modifying
    @Query("DELETE FROM FcmToken ft WHERE ft.userId = :userId")
    Long deleteAllByUserId(Long userId);
}
