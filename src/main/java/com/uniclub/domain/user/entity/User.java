package com.uniclub.domain.user.entity;

import com.uniclub.global.entity.BaseTime;
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
    }

    public void updateInfo(String name, String major) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (major != null && !major.isBlank()) {
            this.major = major;
        }
    }


}
