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
import com.uniclub.global.s3.S3ServiceImpl;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
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
    private final S3ServiceImpl s3ServiceImpl;

    //동아리 목록 조회
    @Transactional(readOnly = true)
    public Slice<ClubResponseDto> getClubs(
            Long userId, String category, String sortBy, String cursorName, int size) {

        // String -> Enum 타입 변경, 카테고리 null인 경우 전체 동아리 조회
        CategoryType categoryName = (category == null) ? null : EnumConverter.stringToEnum(category, CategoryType.class, ErrorCode.CATEGORY_NOT_FOUND);
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
        CategoryType categoryType = EnumConverter.stringToEnum(clubCreateRequestDto.getCategory(), CategoryType.class, ErrorCode.CATEGORY_NOT_FOUND);

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

        ClubStatus clubStatus = EnumConverter.stringToEnum(promotionRegisterRequestDto.getStatus(), ClubStatus.class, ErrorCode.STATUS_NOT_FOUND);

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

        //요청에 CLUB_PROFILE과 CLUB_BACKGROUND이 두개 이상 담겨있는지 확인
        validateMediaType(clubMediaUploadRequestDtoList);

        //기존에 존재하는 CLUB_PROFILE과 CLUB_BACKGROUND 삭제
        deleteExistingUniqueMediaType(clubId, clubMediaUploadRequestDtoList);

        //미디어 저장
        for (ClubMediaUploadRequestDto clubMediaUploadRequestDto : clubMediaUploadRequestDtoList) {
            MediaType mediaType = EnumConverter.stringToEnum(clubMediaUploadRequestDto.getMediaType(), MediaType.class, ErrorCode.MEDIA_TYPE_NOT_FOUND);
            Media media = clubMediaUploadRequestDto.toMediaEntity(club, mediaType);
            mediaRepository.save(media);
        }
    }

    private void validateMediaType(List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        //각 Type들 개수 세기
        Map<String, Long> mediaTypeCount = clubMediaUploadRequestDtoList.stream()
                .collect(Collectors.groupingBy(
                        ClubMediaUploadRequestDto::getMediaType,
                        Collectors.counting()
                ));

        // 단일 허용 MediaType들 검증
        //CLUB_PROFILE
        if (mediaTypeCount.getOrDefault(MediaType.CLUB_PROFILE, 0L) > 1) {
            throw new CustomException(ErrorCode.DUPLICATE_MEDIA_TYPE);
        }
        //CLUB_BACKGROUND
        if (mediaTypeCount.getOrDefault(MediaType.CLUB_BACKGROUND, 0L) > 1) {
            throw new CustomException(ErrorCode.DUPLICATE_MEDIA_TYPE);
        }
    }

    private void deleteExistingUniqueMediaType(Long clubId, List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        //새로 업로드 되는 것 확인
        Set<String> newMediaTypes = clubMediaUploadRequestDtoList.stream()
                .map(ClubMediaUploadRequestDto::getMediaType)
                .collect(Collectors.toSet());

        //CLUB_PROFILE이 새로 업로드될 예정이면 기존 파일 삭제
        if (newMediaTypes.contains(MediaType.CLUB_PROFILE)) {
            mediaRepository.deleteByClubIdAndMediaType(clubId, MediaType.CLUB_PROFILE);
        }

        //CLUB_BACKGROUND가 새로 업로드될 예정이면 기존 파일 삭제
        if (newMediaTypes.contains(MediaType.CLUB_BACKGROUND)) {
            mediaRepository.deleteByClubIdAndMediaType(clubId, MediaType.CLUB_BACKGROUND);
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
            String presignedUrl = s3ServiceImpl.getDownloadPresignedUrl(media.getMediaLink());
            mediaResList.add(DescriptionMediaDto.from(media, presignedUrl));
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


    //특정 동아리 유저 권한 확인 (정보 없으면 GUEST로 반환)
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        return membershipRepository.findByUserIdAndClubId(userId, clubId)
                .map(MemberShip::getRole)
                .orElse(Role.GUEST);
    }




}
