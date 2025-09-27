package com.uniclub.domain.user.entity;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;    //PK

    @Column(nullable = false, length = 50)
    private String name;    //이름

    @Column(nullable = false)
    private String nickname;  // 회원가입창에서 입력 X, 디폴트는 이름

    @Column(nullable = false, length = 30, unique = true)
    private String studentId;   //학번

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Major major;    //전공

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_media_id")
    private Media profileMedia;  // 프로필 이미지

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean notificationEnabled;    //알림설정


    public User(String name, String studentId, Major major, String nickname) {
        this.name = name;
        this.studentId = studentId;
        this.major = major;
        this.nickname = nickname;
    }

    public void updateInfo(String name, Major major, String nickname, Media profileMedia) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (major != null) {
            this.major = major;
        }
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (profileMedia != null) {
            this.profileMedia = profileMedia;
        }
    }

    public void toggleNotification() {
        this.notificationEnabled = !this.notificationEnabled;
    }

    @Override
    public void softDelete() {
        super.softDelete();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        this.studentId = "d_" + timestamp + "_" + this.studentId;
    }

}
