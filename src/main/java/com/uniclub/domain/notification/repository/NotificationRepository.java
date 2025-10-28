package com.uniclub.domain.notification.repository;

import com.uniclub.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId")
    List<Notification> findByUserId(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.notificationId = :notificationId")
    Optional<Notification> findByNotificationId(@Param("notificationId") Long notificationId);

    @Query("SELECT n FROM Notification n WHERE n.notificationId = :notificationId AND n.userId = :userId")
    Optional<Notification> findByNotificationIdAndUserId(Long notificationId, Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.read = :read ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndRead(Long userId, boolean read, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId IN :userIds")
    void deleteByUserIds(List<Long> userIds);
}
