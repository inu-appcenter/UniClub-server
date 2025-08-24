package com.uniclub.domain.club.dto;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.ClubStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "동아리 홍보게시글 생성 및 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubPromotionRegisterRequestDto {

    @Schema(description = "동아리 이름", example = "앱센터")
    private String name;

    @Schema(description = "동아리 모집 현황", example = "SCHEDULED, ACTIVE, CLOSED")
    private String status;

    @Schema(description = "모집 시작 시간", example = "2025-08-03T14:30:00")
    private LocalDateTime startTime;

    @Schema(description = "모집 마감 시간", example = "2025-08-03T14:30:00")
    private LocalDateTime endTime;

    @Schema(description = "동아리 한 줄 소개")
    private String simpleDescription;

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

    @Schema(description = "지원 폼 url")
    private String applicationFormLink;

    //저장을 위해 Club Entity로 변환
    public Club toClubEntity(ClubStatus clubStatus) {
        return Club.builder()
                .name(name)
                .status(clubStatus)
                .startTime(startTime)
                .endTime(endTime)
                .simpleDescription(simpleDescription)
                .description(description)
                .notice(notice)
                .location(location)
                .presidentName(presidentName)
                .presidentPhone(presidentPhone)
                .youtubeLink(youtubeLink)
                .instagramLink(instagramLink)
                .applicationFormLink(applicationFormLink)
                .build();
    }



}
