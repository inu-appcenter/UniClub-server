package com.uniclub.domain.fcm.entity;

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
@Table(name = "fcm_token")
public class FcmToken extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmTokenId;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public FcmToken(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
