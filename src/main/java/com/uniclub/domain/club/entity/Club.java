package com.uniclub.domain.club.entity;

import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private ClubStatus status = ClubStatus.CLOSED;  //모집상태

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

    @Builder
    public Club(String name, ClubStatus status, LocalDateTime startTime, LocalDateTime endTime, String description, String notice, String location, String presidentInfo, String youtubeLink, String instagramLink, String profileImage, String backgroundImage) {
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.notice = notice;
        this.location = location;
        this.presidentInfo = presidentInfo;
        this.youtubeLink = youtubeLink;
        this.instagramLink = instagramLink;
        this.profileImage = profileImage;
        this.backgroundImage = backgroundImage;
    }

    public Club update(Club club) {
        this.status = club.getStatus();
        this.startTime = club.getStartTime();
        this.endTime = club.getEndTime();
        this.description = club.getDescription();
        this.notice = club.getNotice();
        this.location = club.getLocation();
        this.presidentInfo = club.getPresidentInfo();
        this.youtubeLink = club.getYoutubeLink();
        this.instagramLink = club.getInstagramLink();
        this.profileImage = club.getProfileImage();
        this.backgroundImage = club.getBackgroundImage();
        return this;
    }


}
