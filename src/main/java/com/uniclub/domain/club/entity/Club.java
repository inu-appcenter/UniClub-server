package com.uniclub.domain.club.entity;

import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "club")
public class Club extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubId;    //PK

    @Column(nullable = false, length = 20, unique = true)
    private String name;    //동아리명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;  //모집상태

    @Column
    private LocalDateTime startTime;    //모집 시작일시

    @Column
    private LocalDateTime endTime;  //모집 마감일시

    @Column(columnDefinition = "TEXT")
    private String description; //동아리 홀보글

    @Column(length = 50)
    private String notice;  //공지

    @Column(length = 20)
    private String location;    //동아리방 위치

    @Column(length = 50)
    private String presidentInfo;   //회장 정보

    @Column(columnDefinition = "TEXT")
    private String youtubeLink; //유튜브 url

    @Column(columnDefinition = "TEXT")
    private String instagramLink;   //인스타그램 url

    @Column(columnDefinition = "TEXT")
    private String profileImage;    //동아리 프로필 이미지 url

    @Column(columnDefinition = "TEXT")
    private String backgroundImage; //동아리 배경 이미지 url
}
