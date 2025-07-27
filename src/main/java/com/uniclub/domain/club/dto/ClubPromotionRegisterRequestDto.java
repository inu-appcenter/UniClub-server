package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import com.uniclub.domain.club.entity.Media;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ClubPromotionRegisterRequestDto {

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

    private List<String> mediaLink; //이건 Media에 저장

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
                .presidentInfo(clubPromotionRegisterRequestDto.getPresidentInfo())
                .youtubeLink(clubPromotionRegisterRequestDto.getYoutubeLink())
                .instagramLink(clubPromotionRegisterRequestDto.getInstagramLink())
                .profileImage(clubPromotionRegisterRequestDto.getProfileImage())
                .backgroundImage(clubPromotionRegisterRequestDto.getBackgroundImage())
                .build();
    }



}
