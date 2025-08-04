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
    private String name;

    @Schema(description = "동아리 모집 현황", example = "CLOSED")
    private ClubStatus status;

    @Schema(description = "모집 시작 시간", example = "2025-08-03T14:30:00")
    private LocalDateTime startTime;

    @Schema(description = "모집 마감 시간", example = "2025-08-03T14:30:00")
    private LocalDateTime endTime;

    @Schema(description = "소개글")
    private String description;

    @Schema(description = "공지")
    private String notice;

    @Schema(description = "동아리방 위치", example = "4호관 107호")
    private String location;

    @Schema(description = "회장 이름", example = "홍길동")
    private String presidentName;

    @Schema(description = "회장 전화번호", example = "010-1234-5678")
    private String presidentPhone;

    @Schema(description = "유튜브 url")
    private String youtubeLink;

    @Schema(description = "인스타그램 url")
    private String instagramLink;

    @Schema(description = "프로필 이미지 url")
    private String profileImage;

    @Schema(description = "배경 이미지 url")
    private String backgroundImage;

    @Schema(description = "첨부 이미지, 영상 url")
    private List<String> mediaLink; //이건 Media에 저장

    @Builder
    private ClubPromotionResponseDto(Club club, Media media) {
        this.name = club.getName();
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

        //미디어 리스트 추가

    }

    /*
    public static ClubPromotionResponseDto from(Club club, Media media) {
        return ClubPromotionResponseDto.
    }
     */
}
