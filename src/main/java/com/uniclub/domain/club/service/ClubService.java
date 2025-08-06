package com.uniclub.domain.club.service;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionResponseDto;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.favorite.entity.Favorite;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    //동아리 목록 조회
    @Transactional(readOnly = true)
    public Slice<ClubResponseDto> getClubs(
            Long userId, String category, String sortBy, String cursorName, int size) {

        // String -> Enum 타입 변경, 카테고리 null인 경우 전체 동아리 조회
        CategoryType categoryName = (category == null) ? null : CategoryType.from(category);
        Pageable pageable = PageRequest.of(0, size + 1);
        // 정렬 기준 별 동아리 목록 조회
        Slice<Club> clubs = switch (sortBy) {
            case "name" -> clubRepository.findClubsByCursorOrderByName(categoryName, cursorName, pageable);
            case "like" -> clubRepository.findClubsByCursorOrderByFavorite(userId, categoryName, cursorName, pageable);
            case "status" -> clubRepository.findClubsByCursorOrderByStatus(categoryName, cursorName, pageable);

            // 유효하지 않은 정렬 기준 예외처리
            default -> throw new CustomException(ErrorCode.INVALID_SORT_CONDITION);
        };

        // 동아리 목록 추출
        List<Club> clubList = clubs.getContent();
        // 유저가 관심 등록한 동아리 목록
        List<Long> favoriteClubIds = favoriteRepository.findClubIdsByUserId(userId);
        // O(n) -> O(1)
        Set<Long> favoriteSet = new HashSet<>(favoriteClubIds);

        // 추출된 동아리 목록에서 관심등록 여부 확인 후 DTO 리스트 생성
        List<ClubResponseDto> content = new ArrayList<>();
        for (Club club : clubList) {
            boolean isFavorite = favoriteSet.contains(club.getClubId());
            ClubResponseDto dto = ClubResponseDto.from(club, isFavorite);
            content.add(dto);
        }
        return new SliceImpl<>(content, pageable, clubs.hasNext());
    }


    //좋아요 토글링
    public boolean toggleFavorite(Long clubId, UserDetailsImpl userDetails) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
        User user = userDetails.getUser();

        // 관심 동아리인지 확인
        boolean isFavorite = favoriteRepository.existsByUserIdAndClubId(user.getUserId(), clubId);

        // true면 관심동아리 등록, false면 취소
        if (isFavorite) {
            favoriteRepository.deleteByUserAndClub(user, club);
            return false;
        } else {
            favoriteRepository.save(new Favorite(user, club));
            return true;
        }
    }

    //(개발자 전용) 동아리 생성 (이름만 있는 상태)
    public void createClub(ClubCreateRequestDto clubCreateRequestDto) {
        if (clubRepository.existsByName(clubCreateRequestDto.getName())){   //동아리 이름 중복 검증
            throw new CustomException(ErrorCode.DUPLICATE_CLUB_NAME);
        };
        Club club = clubCreateRequestDto.toClubEntity(clubCreateRequestDto);
        clubRepository.save(club);
    }

    //동아리 소개글 작성
    public void saveClubPromotion(UserDetailsImpl userDetails, Long clubId, ClubPromotionRegisterRequestDto promotionRegisterRequestDto) {
        Club existingClub = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        //해당 동아리의 회장인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //동아리 소개글 수정사항 반영
        existingClub.update(promotionRegisterRequestDto.toClubEntity(promotionRegisterRequestDto));

    }


    //동아리 소개글 불러오기
    @Transactional(readOnly = true)
    public ClubPromotionResponseDto getClubPromotion(Long clubId) {
        Club club = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );


        List<Media> medias = mediaRepository.findByClubId(clubId);
        List<String> mediaLinks = new ArrayList<>();
        for (Media media : medias) {
            mediaLinks.add(media.getMediaLink());
        }
        return ClubPromotionResponseDto.from(club, mediaLinks);
    }


    //(개발자 전용) 동아리 삭제
    public void deleteClub(Long clubId) {

        clubRepository.findById(clubId).orElseThrow(
                () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
        );

        clubRepository.deleteById(clubId);
    }

    //동아리 미디어 저장
    private Media saveMedia(String media, Club mediaEntity) {
        return Media.builder()
                .mediaLink(media)
                .club(mediaEntity)
                .build();
    }

    //특정 동아리 유저 권한 확인
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        MemberShip memberShip = membershipRepository.findByUserIdAndClubId(userId, clubId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
        return memberShip.getRole();
    }


}
