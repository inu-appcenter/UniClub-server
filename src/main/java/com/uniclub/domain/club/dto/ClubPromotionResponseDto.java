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

    private final String name;

    private final ClubStatus status;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final String description;

    private final String notice;

    private final String location;

    private final String presidentName;

    private final String presidentPhone;

    private final String youtubeLink;

    private final String instagramLink;

    private final String profileImage;

    private final String backgroundImage;

    private final List<String> mediaLinks;

    @Builder
    public ClubPromotionResponseDto(String name, ClubStatus status, LocalDateTime startTime, LocalDateTime endTime, String description, String notice, String location, String presidentName, String presidentPhone, String youtubeLink, String instagramLink, String profileImage, String backgroundImage, List<String> mediaLinks) {
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
        this.mediaLinks = mediaLinks;
    }


    public static ClubPromotionResponseDto from(Club club, List<String> mediaLinks) {
        return ClubPromotionResponseDto.builder()
                .name(club.getName())
                .status(club.getStatus())
                .startTime(club.getStartTime())
                .endTime(club.getEndTime())
                .description(club.getDescription())
                .notice(club.getNotice())
                .location(club.getLocation())
                .presidentName(club.getPresidentName())
                .presidentPhone(club.getPresidentPhone())
                .youtubeLink(club.getYoutubeLink())
                .instagramLink(club.getInstagramLink())
                .profileImage(club.getProfileImage())
                .backgroundImage(club.getBackgroundImage())
                .mediaLinks(mediaLinks)
                .build();
    }
}
