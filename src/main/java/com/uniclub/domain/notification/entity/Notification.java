package com.uniclub.domain.notification.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;    //PK

    @Column(columnDefinition = "TEXT")
    private String message; //알림 메시지

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean read; //읽음 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;  //알림 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Builder
    public Notification(String message, NotificationType type, User user) {
        this.message = message;
        this.type = type;
        this.user = user;
    }
    
    public void markAsRead() {
        this.read = true;
    }
}
