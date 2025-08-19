package com.uniclub.domain.user.entity;

import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, length = 15, unique = true)
    private String studentId;   //학번

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String major;    //전공


    public User(String name, String studentId, String password, String major) {
        this.name = name;
        this.studentId = studentId;
        this.password = password;
        this.major = major;
        this.nickname = name;
    }

    public void updateInfo(String name, String major, String nickname) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (major != null && !major.isBlank()) {
            this.major = major;
        }
        if(nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
    }


}
