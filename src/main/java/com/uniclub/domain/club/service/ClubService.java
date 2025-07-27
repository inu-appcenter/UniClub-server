package com.uniclub.domain.club.service;

import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MediaRepository mediaRepository;

    //동아리 생성 (이름만 있는 상태)
    public void createClub(ClubCreateRequestDto clubCreateRequestDto) {
        if (clubRepository.existsByName(clubCreateRequestDto.getName())){   //동아리 이름 중복 검증
            throw new RuntimeException();   //임시
        };
        Club club = clubCreateRequestDto.toClubEntity(clubCreateRequestDto);
        clubRepository.save(club);
    }

    //동아리 소개글 작성
    public void saveClubPromotion(/*UserDetails user*/ Long clubId, ClubPromotionRegisterRequestDto promotionRegisterRequestDto) {
        /*
            유저 권한 체크 추가 예정
        */

        Club existingClub = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new RuntimeException("Club not found")    //임시
                );

        //동아리 소개글 수정사항 반영
        existingClub.update(promotionRegisterRequestDto.toClubEntity(promotionRegisterRequestDto));

        //미디어 저장 및 매핑
        List<String> mediaLinks = promotionRegisterRequestDto.getMediaLink();
        for (String mediaLink : mediaLinks) {
            Media media = saveMedia(mediaLink, existingClub);
            mediaRepository.save(media);
        }

    }

    /*
    //동아리 소개글 불러오기 (미완)
    public ClubPromotionResponseDto getClubPromotion(Long clubId) {
        Club club = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new RuntimeException("Club not found")    //임시
                );
        ClubPromotionResponseDto clubPromotionResponseDto = ;
    }
    */
    //동아리 삭제
    public void deleteClub(/*UserDetails user*/Long clubId) {

        clubRepository.findById(clubId).orElseThrow(
                () -> new RuntimeException("Club not found")    //임시
        );

        /*
            유저 권한 확인
        */

        clubRepository.deleteById(clubId);
    }

    //동아리 미디어 저장
    private Media saveMedia(String media, Club mediaEntity) {
        return Media.builder()
                .mediaLink(media)
                .club(mediaEntity)
                .build();
    }


}
