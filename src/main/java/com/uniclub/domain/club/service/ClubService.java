package com.uniclub.domain.club.service;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public List<ClubResponseDto> getAllClubs(User user) {
        return clubRepository.findAll().stream()
                .map(club -> {
                    boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);
                    return ClubResponseDto.from(club, isFavorite);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ClubResponseDto> getClubsByCategory(User user, CategoryType categoryType) {
        return clubRepository.findByCategoryName(categoryType).stream()
                .map(club -> {
                    boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);
                    return ClubResponseDto.from(club, isFavorite);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleFavorite(Long clubId, User user) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동아리입니다."));

        boolean isFavorite = favoriteRepository.existsByUserAndClub(user, club);

        if (isFavorite) {
            favoriteRepository.deleteByUserAndClub(user, club);
            return false;
        } else {
            Favorite favorite = new Favorite(user, club);
            favoriteRepository.save(favorite);
            return true;
        }
    }

}