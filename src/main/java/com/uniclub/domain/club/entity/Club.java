package com.uniclub.domain.club.entity;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
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
    private Long clubId; // PK

    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private ClubStatus status = ClubStatus.CLOSED;  //모집상태

    @Column
    private LocalDateTime startTime;    //모집 시작일시

    @Column
    private LocalDateTime endTime;    //모집 마감일시

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String notice;

    @Column(length = 20)
    private String location;

    @Column(length = 20)
    private String presidentName;

    @Column(length = 15)
    private String presidentPhone;

    @Column(columnDefinition = "TEXT")
    private String youtubeLink;

    @Column(columnDefinition = "TEXT")
    private String instagramLink;

    @Column(columnDefinition = "TEXT")
    private String profileImage;
 
    @Column(columnDefinition = "TEXT")
    private String backgroundImage; //동아리 배경 이미지 url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Club(String name, ClubStatus status, LocalDateTime startTime, LocalDateTime endTime,
                String description, String notice, String location, String presidentName, String presidentPhone,
                String youtubeLink, String instagramLink, String profileImage, String backgroundImage,
                Category category) {
  
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.notice = notice;
        this.location = location;
        this.presidentName = presidentName;
        this.presidentPhone = presidentPhone;
        this.youtubeLink = youtubeLink;
        this.instagramLink = instagramLink;
        this.profileImage = profileImage;
        this.backgroundImage = backgroundImage;
        this.category = category;
    }

    public Club update(Club club) {
        this.status = club.getStatus();
        this.startTime = club.getStartTime();
        this.endTime = club.getEndTime();
        this.description = club.getDescription();
        this.notice = club.getNotice();
        this.location = club.getLocation();
        this.presidentName = club.getPresidentName();
        this.presidentPhone = club.getPresidentPhone();
        this.youtubeLink = club.getYoutubeLink();
        this.instagramLink = club.getInstagramLink();
        this.profileImage = club.getProfileImage();
        this.backgroundImage = club.getBackgroundImage();
        return this;
    }

}
