package com.uniclub.domain.notification.repository;

import com.uniclub.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n JOIN FETCH n.user WHERE n.user.userId = :userId")
    List<Notification> findByUserId(Long userId);
}
