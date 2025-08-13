package com.uniclub.domain.club.service;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.category.repository.CategoryRepository;
import com.uniclub.domain.club.dto.*;
import com.uniclub.domain.club.entity.*;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final CategoryRepository categoryRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    //동아리 목록 조회
    @Transactional(readOnly = true)
    public Slice<ClubResponseDto> getClubs(
            Long userId, String category, String sortBy, String cursorName, int size) {

        // String -> Enum 타입 변경, 카테고리 null인 경우 전체 동아리 조회
        CategoryType categoryName = (category == null) ? null : stringToCategoryType(category);
        Pageable pageable = PageRequest.of(0, size + 1);
        // 정렬 기준 별 동아리 목록 조회
        Slice<Club> clubs = switch (sortBy) {
            case "name" -> clubRepository.findClubsByCursorOrderByName(categoryName, cursorName, pageable);
            case "like" -> clubRepository.findClubsByCursorOrderByFavorite(userId, categoryName, cursorName, pageable);
            case "status" -> clubRepository.findClubsByCursorOrderByStatus(categoryName, cursorName, pageable);

            // 유효하지 않은 정렬 기준 예외처리
            default -> throw new CustomException(ErrorCode.INVALID_SORT_CONDITION);
        };
        boolean hasNext = clubs.hasNext();

        // 동아리 목록 추출 및 페이지 사이즈와 리스트 요소 개수 일치하도록 조정
        List<Club> clubList = new ArrayList<>(clubs.getContent());
        if (hasNext){
            clubList.removeLast();
        }

        // 유저가 관심 등록한 동아리 목록
        Set<Long> favoriteSet = new HashSet<>(
                favoriteRepository.findClubIdsByUserId(userId)
        );

        // 추출된 동아리 목록에서 관심등록 여부 확인 후 DTO 리스트 생성
        List<ClubResponseDto> clubResponseDtoList = new ArrayList<>();
        for (Club club : clubList) {
            boolean isFavorite = favoriteSet.contains(club.getClubId());
            ClubResponseDto dto = ClubResponseDto.from(club, isFavorite);
            clubResponseDtoList.add(dto);
        }
        return new SliceImpl<>(clubResponseDtoList, pageable, hasNext);
    }


    //좋아요 토글링
    public ToggleFavoriteResponseDto toggleFavorite(Long clubId, UserDetailsImpl userDetails) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
        User user = userDetails.getUser();

        // 관심 동아리인지 확인
        boolean isFavorite = favoriteRepository.existsByUserIdAndClubId(user.getUserId(), clubId);

        if (isFavorite) {
            favoriteRepository.deleteByUserAndClub(user, club);
            return new ToggleFavoriteResponseDto("관심 동아리 등록 취소 완료");
        } else {
            favoriteRepository.save(new Favorite(user, club));
            return new ToggleFavoriteResponseDto("관심 동아리 등록 완료");
        }
    }

    //(개발자 전용) 동아리 생성 (이름만 있는 상태)
    public void createClub(ClubCreateRequestDto clubCreateRequestDto) {
        if (clubRepository.existsByName(clubCreateRequestDto.getName())){   //동아리 이름 중복 검증
            throw new CustomException(ErrorCode.DUPLICATE_CLUB_NAME);
        };

        // String -> categoryType 변환
        CategoryType categoryType = stringToCategoryType(clubCreateRequestDto.getCategory());

        Category category = new Category(categoryType);

        Club club = clubCreateRequestDto.toClubEntity(category);
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

        ClubStatus clubStatus = stringToClubStatus(promotionRegisterRequestDto.getStatus());

        //동아리 소개글 수정사항 반영
        existingClub.update(promotionRegisterRequestDto.toClubEntity(clubStatus));

    }

    public void uploadClubMedia(UserDetailsImpl userDetails, Long clubId, List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        //해당 동아리의 운영진인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT && userRole != Role.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //validateRequestDuplicates();

        //미디어 저장
        for (ClubMediaUploadRequestDto clubMediaUploadRequestDto : clubMediaUploadRequestDtoList) {
            MediaType mediaType = stringToMediaType(clubMediaUploadRequestDto.getMediaType());
            Media media = clubMediaUploadRequestDto.toMediaEntity(club, mediaType);
            mediaRepository.save(media);
        }
    }


    //동아리 소개글 불러오기
    @Transactional(readOnly = true)
    public ClubPromotionResponseDto getClubPromotion(UserDetailsImpl userDetails, Long clubId) {
        Club club = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        List<Media> mediaList = mediaRepository.findByClubId(clubId);
        List<DescriptionMediaDto> mediaResList = new ArrayList<>();
        for (Media media : mediaList) {
            mediaResList.add(DescriptionMediaDto.from(media));
        }
        return ClubPromotionResponseDto.from(checkRole(userDetails.getUserId(), clubId), club, mediaResList);
    }


    //(개발자 전용) 동아리 삭제
    public void deleteClub(Long clubId) {

        clubRepository.findById(clubId).orElseThrow(
                () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
        );

        clubRepository.deleteById(clubId);
    }


    //특정 동아리 유저 권한 확인
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        MemberShip memberShip = membershipRepository.findByUserIdAndClubId(userId, clubId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
        return memberShip.getRole();
    }

    //동아리 프로필, 배경 이미지 유효성 검사
    private void validateRequestDuplicates(List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        Map<MediaType, Long> typeCount = clubMediaUploadRequestDtoList.stream()
                .collect(Collectors.groupingBy(
                        dto -> stringToMediaType(dto.getMediaType()), // String → MediaType 변환
                        Collectors.counting()
                ));

        for (Map.Entry<MediaType, Long> entry : typeCount.entrySet()) {
            MediaType type = entry.getKey();
            Long count = entry.getValue();

            if ((type == MediaType.CLUB_PROMOTION || type == MediaType.CLUB_PROFILE) && count > 1) {
                throw new CustomException(ErrorCode.DUPLICATE_MEDIA_TYPE);
            }
        }
    }

    private CategoryType stringToCategoryType(String input) {
        for (CategoryType category : CategoryType.values()) {
            if (category.name().equals(input)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
    }

    private ClubStatus stringToClubStatus(String input) {
        for (ClubStatus clubStatus : ClubStatus.values()) {
            if (clubStatus.name().equals(input)) {
                return clubStatus;
            }
        }
        throw new CustomException(ErrorCode.STATUS_NOT_FOUND);
    }

    private MediaType stringToMediaType(String input) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.name().equals(input)) {
                return mediaType;
            }
        }
        throw new CustomException(ErrorCode.MEDIA_TYPE_NOT_FOUND);
    }

}
