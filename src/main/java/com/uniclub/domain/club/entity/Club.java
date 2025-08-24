package com.uniclub.domain.club.entity;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    @Column(length = 50)
    private String simpleDescription;  // 동아리 한줄 소개

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String notice;

    @Column(length = 20)
    private String location;

    @Column(length = 50)
    private String presidentName;

    @Column(length = 15)
    private String presidentPhone;

    @Column(columnDefinition = "TEXT")
    private String youtubeLink;

    @Column(columnDefinition = "TEXT")
    private String instagramLink;

    @Column(columnDefinition = "TEXT")
    private String applicationFormLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Club(String name, ClubStatus status, LocalDateTime startTime, LocalDateTime endTime, String simpleDescription,
                String description, String notice, String location, String presidentName, String presidentPhone,
                String youtubeLink, String instagramLink, String applicationFormLink, Category category) {
  
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.simpleDescription = simpleDescription;
        this.description = description;
        this.notice = notice;
        this.location = location;
        this.presidentName = presidentName;
        this.presidentPhone = presidentPhone;
        this.youtubeLink = youtubeLink;
        this.instagramLink = instagramLink;
        this.applicationFormLink = applicationFormLink;
        this.category = category;
    }

    public Club update(Club club) {
        this.status = club.getStatus();
        this.startTime = club.getStartTime();
        this.endTime = club.getEndTime();
        this.simpleDescription = club.getSimpleDescription();
        this.description = club.getDescription();
        this.notice = club.getNotice();
        this.location = club.getLocation();
        this.presidentName = club.getPresidentName();
        this.presidentPhone = club.getPresidentPhone();
        this.youtubeLink = club.getYoutubeLink();
        this.instagramLink = club.getInstagramLink();
        this.applicationFormLink = club.getApplicationFormLink();
        return this;
    }

}
