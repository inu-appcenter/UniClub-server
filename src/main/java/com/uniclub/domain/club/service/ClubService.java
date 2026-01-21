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
import com.uniclub.global.s3.S3Service;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final CategoryRepository categoryRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public Slice<ClubResponseDto> getClubs(Long userId, String category, String sortBy, String cursorName, int size) {

        CategoryType categoryType = (category == null) ? null :
                EnumConverter.stringToEnum(category, CategoryType.class, ErrorCode.CATEGORY_NOT_FOUND);

        List<Club> clubs = findClubsSorted(userId, categoryType, sortBy, cursorName, size);

        boolean hasNext = clubs.size() > size;
        if (hasNext) {
            clubs = clubs.subList(0, size);
        }

        Set<Long> favoriteClubIds = new HashSet<>(favoriteRepository.findClubIdsByUserId(userId));
        Map<Long, String> profileMap = buildProfileMap(clubs);

        return toClubResponseSlice(clubs, favoriteClubIds, profileMap, hasNext, size);
    }


    //좋아요 토글링
    public ToggleFavoriteResponseDto toggleFavorite(Long clubId, UserDetailsImpl userDetails) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 삭제된 동아리인지 확인
        if (club.isDeleted()){
            throw new CustomException(ErrorCode.CLUB_DELETED);
        }

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
        log.info("동아리 생성 시작: 동아리명={}", clubCreateRequestDto.getName());
        if (clubRepository.existsByName(clubCreateRequestDto.getName())){   //동아리 이름 중복 검증
            throw new CustomException(ErrorCode.DUPLICATE_CLUB_NAME);
        }

        // String -> categoryType 변환
        CategoryType categoryType = EnumConverter.stringToEnum(clubCreateRequestDto.getCategory(), CategoryType.class, ErrorCode.CATEGORY_NOT_FOUND);

        //DB에서 category조회
        Category category = categoryRepository.findByName(categoryType)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Club club = clubCreateRequestDto.toClubEntity(category);
        clubRepository.save(club);
        log.info("동아리 생성 성공: 동아리명={}", clubCreateRequestDto.getName());
    }


    //동아리 소개글 작성
    public void saveClubPromotion(UserDetailsImpl userDetails, Long clubId, ClubPromotionRegisterRequestDto promotionRegisterRequestDto) {
        log.info("동아리 소개글 작성 시작: clubId={}, userId={}", clubId, userDetails.getUserId());
        Club existingClub = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        // 삭제된 동아리인지 확인
        if (existingClub.isDeleted()){
            throw new CustomException(ErrorCode.CLUB_DELETED);
        }

        //해당 동아리의 회장인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        ClubStatus clubStatus = EnumConverter.stringToEnum(promotionRegisterRequestDto.getStatus(), ClubStatus.class, ErrorCode.STATUS_NOT_FOUND);

        //동아리 소개글 수정 사항 반영
        existingClub.updatePromotion(
                clubStatus,
                promotionRegisterRequestDto.getStartTime(),
                promotionRegisterRequestDto.getEndTime(),
                promotionRegisterRequestDto.getSimpleDescription(),
                promotionRegisterRequestDto.getDescription(),
                promotionRegisterRequestDto.getNotice(),
                promotionRegisterRequestDto.getLocation(),
                promotionRegisterRequestDto.getPresidentName(),
                promotionRegisterRequestDto.getPresidentPhone(),
                promotionRegisterRequestDto.getYoutubeLink(),
                promotionRegisterRequestDto.getInstagramLink(),
                promotionRegisterRequestDto.getApplicationFormLink()
        );

        log.info("동아리 소개글 작성 완료: clubId={}, userId={}, status={}", clubId, userDetails.getUserId(), clubStatus);
    }


    //동아리 미디어 파일 업로드
    public void uploadClubMedia(UserDetailsImpl userDetails, Long clubId, List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        log.info("동아리 미디어 업로드 시작: clubId={}, userId={}", clubId, userDetails.getUserId());
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 삭제된 동아리인지 확인
        if (club.isDeleted()){
            throw new CustomException(ErrorCode.CLUB_DELETED);
        }

        //해당 동아리의 운영진인지 확인
        Role userRole = checkRole(userDetails.getUserId(), clubId);
        if (userRole != Role.PRESIDENT && userRole != Role.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //요청에 CLUB_PROFILE과 CLUB_BACKGROUND이 두개 이상 담겨있는지 확인
        validateMediaType(clubMediaUploadRequestDtoList);

        //기존에 존재하는 CLUB_PROFILE과 CLUB_BACKGROUND 삭제
        deleteExistingUniqueMediaType(clubId, clubMediaUploadRequestDtoList);

        //업로드된 미디어 정보를 담을 리스트
        List<String> uploadedMediaInfo = new ArrayList<>();

        //미디어 저장
        for (ClubMediaUploadRequestDto clubMediaUploadRequestDto : clubMediaUploadRequestDtoList) {
            MediaType mediaType = EnumConverter.stringToEnum(clubMediaUploadRequestDto.getMediaType(), MediaType.class, ErrorCode.MEDIA_TYPE_NOT_FOUND);
            Media media = clubMediaUploadRequestDto.toMediaEntity(club, mediaType);
            mediaRepository.save(media);
            log.info("동아리 미디어 DB 저장 완료: 메인 이미지 여부 = {}", media.isMainMedia());

            // 업로드된 미디어 정보 추가 (URL에서 파일명만 추출)
            String fileName = extractFileName(clubMediaUploadRequestDto.getMediaLink());
            uploadedMediaInfo.add(mediaType + ":" + fileName);
        }
        log.info("동아리 미디어 업로드 완료: clubId={}, userId={}, upload_media={}", clubId, userDetails.getUserId(), uploadedMediaInfo);
    }


    //동아리 소개글 불러오기
    @Transactional(readOnly = true)
    public ClubPromotionResponseDto getClubPromotion(UserDetailsImpl userDetails, Long clubId) {
        Club club = clubRepository.findById(clubId) //실제 존재하는 동아리인지 확인
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        // 삭제된 동아리인지 확인
        if (club.isDeleted()){
            throw new CustomException(ErrorCode.CLUB_DELETED);
        }


        List<Media> mediaList = mediaRepository.findByClubId(clubId);
        boolean favorite = favoriteRepository.existsByUserIdAndClubId(userDetails.getUserId(), clubId);
        List<DescriptionMediaDto> mediaResList = new ArrayList<>();
        for (Media media : mediaList) {
            String presignedUrl = s3Service.getDownloadPresignedUrl(media.getMediaLink());
            mediaResList.add(DescriptionMediaDto.from(media, presignedUrl));
        }
        return ClubPromotionResponseDto.from(checkRole(userDetails.getUserId(), clubId), club, favorite, mediaResList);
    }


    //(개발자 전용) 동아리 삭제
    public void deleteClub(Long clubId) {
        log.info("동아리 삭제 시작: clubId={}", clubId);
        Club club = clubRepository.findById(clubId).orElseThrow(
                () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
        );

        // 삭제된 동아리인지 확인
        if (club.isDeleted()){
            throw new CustomException(ErrorCode.CLUB_DELETED);
        }

        club.softDelete();
        log.info("동아리 삭제 성공: clubId={}", clubId);
    }


    //특정 동아리 유저 권한 확인 (정보 없으면 GUEST로 반환)
    @Transactional(readOnly = true)
    public Role checkRole(Long userId, Long clubId) {
        return membershipRepository.findByUserIdAndClubId(userId, clubId)
                .map(MemberShip::getRole)
                .orElse(Role.GUEST);
    }

    private List<Club> findClubsSorted(Long userId, CategoryType categoryType, String sortBy, String cursorName, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);
        return switch (sortBy) {
            case "name" -> clubRepository.findClubsByCursorOrderByName(categoryType, cursorName, pageable);
            case "like" -> clubRepository.findClubsByCursorOrderByFavorite(userId, categoryType, cursorName, pageable);
            case "status" -> clubRepository.findClubsByCursorOrderByStatus(categoryType, cursorName, pageable);
            default -> throw new CustomException(ErrorCode.INVALID_SORT_CONDITION);
        };
    }

    private Map<Long, String> buildProfileMap(List<Club> clubs) {
        List<Long> clubIds = clubs.stream()
                .map(Club::getClubId)
                .collect(Collectors.toList());

        return mediaRepository.findClubProfilesByClubIds(clubIds).stream()
                .collect(Collectors.toMap(
                        media -> media.getClub().getClubId(),
                        media -> s3Service.getDownloadPresignedUrl(media.getMediaLink())
                ));
    }

    private Slice<ClubResponseDto> toClubResponseSlice(
            List<Club> clubs, Set<Long> favoriteClubIds, Map<Long, String> profileMap, boolean hasNext, int size) {

        List<ClubResponseDto> dtos = clubs.stream()
                .map(club -> ClubResponseDto.from(
                        club,
                        favoriteClubIds.contains(club.getClubId()),
                        profileMap.getOrDefault(club.getClubId(), "")))
                .collect(Collectors.toList());

        return new SliceImpl<>(dtos, PageRequest.of(0, size), hasNext);
    }

    // URL에서 파일명만 추출하는 헬퍼 메서드
    private String extractFileName(String mediaLink) {
        return mediaLink.substring(mediaLink.lastIndexOf('/') + 1);
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
        log.info("중복 미디어 타입 검증 성공");
    }

    private void deleteExistingUniqueMediaType(Long clubId, List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        //새로 업로드 되는 것 확인
        Set<String> newMediaTypes = clubMediaUploadRequestDtoList.stream()
                .map(ClubMediaUploadRequestDto::getMediaType)
                .collect(Collectors.toSet());

        //CLUB_PROFILE이 새로 업로드될 예정이면 기존 파일 삭제
        if (newMediaTypes.contains(MediaType.CLUB_PROFILE)) {
            mediaRepository.deleteByClubIdAndMediaType(clubId, MediaType.CLUB_PROFILE);
            log.info("기존 동아리 프로필 이미지 삭제: clubId={}", clubId);
        }

        //CLUB_BACKGROUND가 새로 업로드될 예정이면 기존 파일 삭제
        if (newMediaTypes.contains(MediaType.CLUB_BACKGROUND)) {
            mediaRepository.deleteByClubIdAndMediaType(clubId, MediaType.CLUB_BACKGROUND);
            log.info("기존 동아리 배경 이미지 삭제: clubId={}", clubId);
        }
    }


}
