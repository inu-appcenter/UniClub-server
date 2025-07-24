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
    private String studentNumber;   //학번

    @Column(nullable = false, length = 20)
    private String major;    //전공


}
