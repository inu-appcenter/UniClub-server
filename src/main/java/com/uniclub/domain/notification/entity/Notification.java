package com.uniclub.domain.notification.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    private Boolean isRead; //읽음 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;  //알림 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    private User user;
}
