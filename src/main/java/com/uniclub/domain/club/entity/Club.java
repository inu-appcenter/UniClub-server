package com.uniclub.domain.club.entity;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

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
    private ClubStatus status = ClubStatus.CLOSED;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String notice;

    @Column(length = 20)
    private String location;

    @Column(length = 50)
    private String presidentInfo;

    @Column(columnDefinition = "TEXT")
    private String youtubeLink;

    @Column(columnDefinition = "TEXT")
    private String instagramLink;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String backgroundImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Club(String name, ClubStatus status, LocalDateTime startTime, LocalDateTime endTime,
                String description, String notice, String location, String presidentInfo,
                String youtubeLink, String instagramLink, String profileImage, String backgroundImage,
                Category category) {
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
        this.category = category;
    }
}
