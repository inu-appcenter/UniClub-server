package com.uniclub.domain.search.service;

import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.global.s3.S3ServiceImpl;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final ClubRepository clubRepository;
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;
    private final S3ServiceImpl s3ServiceImpl;


    @Transactional(readOnly = true)
    public List<ClubResponseDto> search(UserDetailsImpl userDetails, String keyword) {
        List<Club> clubs = clubRepository.findByKeyword(keyword);   //키워드를 통해 동아리 명, 설명글을 조회하여 관련 동아리 목록 가져오기

        List<Long> favorites = favoriteRepository.findClubIdsByUserId(userDetails.getUserId());    //좋아요 누른 동아리 아이디 조회

        List<ClubResponseDto> clubResponseDtoList = new ArrayList<>();
        Set<Long> favoriteSet = new HashSet<>(favorites);   //성능 최적화 시간 복잡도 O(n) -> O(1)

        for (Club club : clubs) {
            boolean isFavorite = favoriteSet.contains(club.getClubId());    //favortes에 clubId가 있으면 true
            
            // 동아리 프로필 이미지 조회
            Media clubProfileMedia = mediaRepository.findByClubIdAndMediaType(club.getClubId(), MediaType.CLUB_PROFILE);
            String clubProfileUrl = "";
            if (clubProfileMedia != null) {
                clubProfileUrl = s3ServiceImpl.getDownloadPresignedUrl(clubProfileMedia.getMediaLink());
            }
            
            clubResponseDtoList.add(ClubResponseDto.from(club, isFavorite, clubProfileUrl));
        }

        return clubResponseDtoList;
    }
}