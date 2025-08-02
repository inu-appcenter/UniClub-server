package com.uniclub.domain.club.service;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    @Transactional(readOnly = true)
    public List<ClubResponseDto> getAllClubs(User user) {
        return clubRepository.findAll().stream()
                .map(club -> {
                    boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);
                    return ClubResponseDto.from(club, isFavorite);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClubResponseDto> getClubsByCategory(User user, CategoryType categoryType) {
        return clubRepository.findByCategoryName(categoryType).stream()
                .map(club -> {
                    boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);
                    return ClubResponseDto.from(club, isFavorite);
                })
                .collect(Collectors.toList());
    }

    public boolean toggleFavorite(Long clubId, User user) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);

        if (isFavorite) {
            favoriteRepository.deleteByUserAndClub(user, club);
            return false;
        } else {
            favoriteRepository.save(new Favorite(user, club));
            return true;
        }
    }

    //동아리 생성 (이름만 있는 상태)
    public void createClub(ClubCreateRequestDto clubCreateRequestDto) {
        if (clubRepository.existsByName(clubCreateRequestDto.getName())){   //동아리 이름 중복 검증
            throw new CustomException(ErrorCode.DUPLICATE_CLUB_NAME);   //임시
        };
        Club club = clubCreateRequestDto.toClubEntity(clubCreateRequestDto);
        clubRepository.save(club);
    }

    //동아리 소개글 작성
    public void saveClubPromotion(UserDetailsImpl user, Long clubId, ClubPromotionRegisterRequestDto promotionRegisterRequestDto) {
        /*
            유저 권한 체크 추가 예정
        */

        Club existingClub = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
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
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)  //임시
                );
        ClubPromotionResponseDto clubPromotionResponseDto = ;
    }
    */

    //동아리 삭제
    public void deleteClub(UserDetailsImpl user, Long clubId) {

        clubRepository.findById(clubId).orElseThrow(
                () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
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
