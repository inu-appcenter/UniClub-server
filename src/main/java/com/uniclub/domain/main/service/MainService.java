package com.uniclub.domain.main.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.domain.main.dto.MainMediaUploadRequestDto;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.main.dto.MainPageMediaResponseDto;
import com.uniclub.global.s3.S3Service;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MainService {

    private final ClubRepository clubRepository;
    private final MediaRepository mediaRepository;
    private final FavoriteRepository favoriteRepository;
    private final S3Service s3Service;



    //메인 페이지 베너 호출
    @Transactional(readOnly = true)
    public List<MainPageMediaResponseDto> getMainPageMedia() {
        List<Media> mainPageMediaList = mediaRepository.findByMediaType(MediaType.MAIN_PAGE);
        List<MainPageMediaResponseDto> mainPageMediaDtoList = new ArrayList<>();
        for (Media media : mainPageMediaList) {
            String presignedUrl = s3Service.getDownloadPresignedUrl(media.getMediaLink());
            mainPageMediaDtoList.add(MainPageMediaResponseDto.from(media, presignedUrl));
        }
        return mainPageMediaDtoList;
    }


    //메인 페이지 동아리 목록 호출
    @Transactional(readOnly = true)
    public List<MainPageClubResponseDto> getMainPageClubs(UserDetailsImpl userDetails){

        // JOIN으로 좋아요 정보, S3 키 모두 가져옴
        List<MainPageClubResponseDto> clubList =
                clubRepository.getMainPageClubs(userDetails.getUserId(), PageRequest.of(0,6));
        
        // 이미지 URL만 presigned URL로 변환
        List<MainPageClubResponseDto> mainPageClubResponseDtoList = new ArrayList<>();
        for (MainPageClubResponseDto dto : clubList) {
            String imageUrl = dto.getImageUrl();
            if (imageUrl != null) {
                imageUrl = s3Service.getDownloadPresignedUrl(imageUrl);
            }

            // S3 키 -> presigned url를 가진 Dto 생성 후 반환
            MainPageClubResponseDto responseDto = MainPageClubResponseDto.builder()
                    .clubId(dto.getClubId())
                    .name(dto.getName())
                    .imageUrl(imageUrl)
                    .favorite(dto.isFavorite())
                    .build();

            mainPageClubResponseDtoList.add(responseDto);
        }
        return mainPageClubResponseDtoList;
    }


    //메인 페이지에 올라가는 미디어 저장
    public void uploadMainMedia(List<MainMediaUploadRequestDto> mainMediaUploadRequestDtoList) {
        log.info("메인페이지 미디어 업로드");

        //업로드된 미디어 정보를 담을 리스트
        List<String> uploadedMediaInfo = new ArrayList<>();

        //미디어 저장
        for (MainMediaUploadRequestDto mainMediaUploadRequestDto : mainMediaUploadRequestDtoList) {
            Media media = mainMediaUploadRequestDto.toMediaEntity();
            mediaRepository.save(media);

            // 업로드된 미디어 정보 추가 (URL에서 파일명만 추출)
            String fileName = extractFileName(mainMediaUploadRequestDto.getMediaLink());
            uploadedMediaInfo.add("MAIN_PAGE" + ":" + fileName);
        }
        log.info("메인페이지 미디어 업로드 완료: upload_media={}", uploadedMediaInfo);
    }


    // URL에서 파일명만 추출하는 헬퍼 메서드
    private String extractFileName(String mediaLink) {
        return mediaLink.substring(mediaLink.lastIndexOf('/') + 1);
    }
}
