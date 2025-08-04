package com.uniclub.domain.club.dto;


import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import com.uniclub.domain.club.entity.Media;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "동아리 홍보게시글 생성 및 수정 응답 DTO")
@Getter
public class ClubPromotionResponseDto {

    @Schema(description = "동아리 이름", example = "앱센터")
    private final String name;

    @Schema(description = "동아리 모집 현황", example = "CLOSED")
    private final ClubStatus status;

    @Schema(description = "모집 시작 시간", example = "2025-08-03T14:30:00")
    private final LocalDateTime startTime;

    @Schema(description = "모집 마감 시간", example = "2025-08-03T14:30:00")
    private final LocalDateTime endTime;

    @Schema(description = "소개글")
    private final String description;

    @Schema(description = "공지")
    private final String notice;

    @Schema(description = "동아리방 위치", example = "4호관 107호")
    private final String location;

    @Schema(description = "회장 이름", example = "홍길동")
    private final String presidentName;

    @Schema(description = "회장 전화번호", example = "010-1234-5678")
    private final String presidentPhone;

    @Schema(description = "유튜브 url")
    private final String youtubeLink;

    @Schema(description = "인스타그램 url")
    private final String instagramLink;

    @Schema(description = "프로필 이미지 url")
    private final String profileImage;

    @Schema(description = "배경 이미지 url")
    private final String backgroundImage;

    @Schema(description = "첨부 이미지, 영상 url")
    private final List<String> mediaLinks; //이건 Media에 저장

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
