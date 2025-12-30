package com.uniclub.domain.notification.repository;

import com.uniclub.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndRead(Long userId, Boolean read, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.notificationId = :notificationId AND n.userId = :userId")
    Optional<Notification> findByNotificationIdAndUserId(Long notificationId, Long userId);

    //int로 변경시 추후 개수 반환 가능
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId IN :userIds")
    void deleteByUserIds(List<Long> userIds);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.read = true AND n.deleted = false")
    void deleteAllByUserIdAndReadTrue(Long userId);
}
