package com.uniclub.domain.club.entity;

import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "TEXT")
    private Boolean isMain; //대표 이미지 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")    //FK
    private Club club;
}
