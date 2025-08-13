package com.uniclub.domain.main.service;

import com.uniclub.domain.club.dto.ClubMediaUploadRequestDto;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.main.dto.MainMediaUploadRequestDto;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.main.dto.MainPageMediaResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MainService {

    private final ClubRepository clubRepository;
    private final MediaRepository mediaRepository;


    @Transactional(readOnly = true)
    public List<MainPageMediaResponseDto> getMainPageMedia() {
        List<Media> mainPageMediaList = mediaRepository.findByMediaType(MediaType.MAIN_PAGE);
        List<MainPageMediaResponseDto> mainPageMediaDtoList = new ArrayList<>();
        for (Media media : mainPageMediaList) {
            mainPageMediaDtoList.add(MainPageMediaResponseDto.from(media));
        }
        return mainPageMediaDtoList;
    }

    @Transactional(readOnly = true)
    public List<MainPageClubResponseDto> getMainPageClubs(UserDetailsImpl userDetails){
        return clubRepository.getMainPageClubs(PageRequest.of(0,6));
    }


    //메인 페이지에 올라가는 미디어 저장
    public void uploadMainMedia(List<MainMediaUploadRequestDto> mainMediaUploadRequestDtoList) {
        //미디어 저장
        for (MainMediaUploadRequestDto mainMediaUploadRequestDto : mainMediaUploadRequestDtoList) {
            Media media = mainMediaUploadRequestDto.toMediaEntity();
            mediaRepository.save(media);
        }
    }
}
