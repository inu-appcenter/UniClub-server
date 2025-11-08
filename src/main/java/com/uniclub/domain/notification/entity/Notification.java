package com.uniclub.domain.notification.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;    //PK

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column
    private Long targetId;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public Notification(String title, String message, NotificationType notificationType, Long targetId, Long userId) {
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.targetId = targetId;
        this.userId = userId;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

}
