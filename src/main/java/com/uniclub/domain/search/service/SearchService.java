package com.uniclub.domain.search.service;

import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
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


    @Transactional(readOnly = true)
    public List<ClubResponseDto> search(UserDetailsImpl user, String keyword) {
        List<Club> clubs = clubRepository.findByKeyword(keyword);   //키워드를 통해 동아리 명, 설명글을 조회하여 관련 동아리 목록 가져오기

        List<Long> favorites = favoriteRepository.findClubIdsByUserId(user.getUserId());    //좋아요 누른 동아리 아이디 조회

        List<ClubResponseDto> clubResponseDtoList = new ArrayList<>();
        Set<Long> favoriteSet = new HashSet<>(favorites);   //성능 최적화 시간 복잡도 O(n) -> O(1)

        for (Club club : clubs) {
            boolean isFavorite = favoriteSet.contains(club.getClubId());    //favortes에 clubId가 있으면 true
            clubResponseDtoList.add(ClubResponseDto.from(club,isFavorite));
        }

        return clubResponseDtoList;
    }
}