package com.uniclub.domain.club.dto;


import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import com.uniclub.domain.club.entity.Media;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ClubPromotionResponseDto {
    private String name;

    private ClubStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String notice;

    private String location;

    private String presidentInfo;

    private String youtubeLink;

    private String instagramLink;

    private String profileImage;

    private String backgroundImage;

    private List<String> mediaLink;

    @Builder
    private ClubPromotionResponseDto(Club club, Media media) {
        this.name = club.getName();
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

        //미디어 리스트 추가

    }

    /*
    public static ClubPromotionResponseDto from(Club club, Media media) {
        return ClubPromotionResponseDto.
    }
     */
}
