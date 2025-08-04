package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import com.uniclub.domain.club.entity.Media;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "동아리 홍보게시글 생성 및 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubPromotionRegisterRequestDto {

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
    private List<String> mediaLinks; //이건 Media에 저장

    //저장을 위해 Club Entity로 변환
    public Club toClubEntity(ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto) {
        return Club.builder()
                .name(clubPromotionRegisterRequestDto.getName())
                .status(clubPromotionRegisterRequestDto.getStatus())
                .startTime(clubPromotionRegisterRequestDto.getStartTime())
                .endTime(clubPromotionRegisterRequestDto.getEndTime())
                .description(clubPromotionRegisterRequestDto.getDescription())
                .notice(clubPromotionRegisterRequestDto.getNotice())
                .location(clubPromotionRegisterRequestDto.getLocation())
                .presidentName(clubPromotionRegisterRequestDto.getPresidentName())
                .presidentPhone(clubPromotionRegisterRequestDto.getPresidentPhone())
                .youtubeLink(clubPromotionRegisterRequestDto.getYoutubeLink())
                .instagramLink(clubPromotionRegisterRequestDto.getInstagramLink())
                .profileImage(clubPromotionRegisterRequestDto.getProfileImage())
                .backgroundImage(clubPromotionRegisterRequestDto.getBackgroundImage())
                .build();
    }



}
