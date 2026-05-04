package com.uniclub.domain.fcm.repository;

import com.uniclub.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    @Query("SELECT ft.token FROM FcmToken ft " +
            "JOIN User u ON ft.userId = u.userId " +
            "WHERE ft.userId IN :userIds " +
            "AND u.notificationEnabled = true " +
            "AND u.deleted = false")
    List<String> findTokensByUserIds(List<Long> userIds);

    @Modifying
    @Query(value =
            "INSERT INTO fcm_token (token, user_id, created_at, update_at, deleted, deleted_at) " +
            "VALUES (:token, :userId, NOW(), NOW(), 0, NULL) " +
            "ON DUPLICATE KEY UPDATE user_id = :userId, update_at = NOW()",
            nativeQuery = true)
    void upsertFcmToken(String token, Long userId);

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token = :token")
    void deleteByToken(String token);

    List<FcmToken> userId(Long userId);

    @Modifying
    @Query("DELETE FROM FcmToken ft WHERE ft.userId = :userId")
    Long deleteAllByUserId(Long userId);
}
