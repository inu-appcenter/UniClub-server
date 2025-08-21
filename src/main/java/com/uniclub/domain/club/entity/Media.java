package com.uniclub.domain.club.entity;

import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "media")
public class Media extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;    //PK

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mediaLink;   //소개글 이미지, 영상 url

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;    //미디어 타입

    @ColumnDefault("false")
    private boolean isMain; //대표 이미지 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId", nullable = true)    //FK
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Club club;

    @Builder
    public Media(String mediaLink, MediaType mediaType, boolean isMain, Club club) {
        this.mediaLink = mediaLink;
        this.mediaType = mediaType;
        this.isMain = isMain;
        this.club = club;
    }

    public void updateIsMain(Boolean isMain) {
        this.isMain = isMain;
    }
}
