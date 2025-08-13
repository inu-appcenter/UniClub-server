package com.uniclub.domain.main.controller;

import com.uniclub.domain.club.dto.ClubMediaUploadRequestDto;
import com.uniclub.domain.main.dto.MainMediaUploadRequestDto;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.main.dto.MainPageMediaResponseDto;
import com.uniclub.domain.main.service.MainService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.MainApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController implements MainApiSpecification {
    private final MainService mainService;

    @GetMapping
    public ResponseEntity<List<MainPageMediaResponseDto>> getMainPageClub() {
        List<MainPageMediaResponseDto> mainPageMediaResponseDtoList = mainService.getMainPageMedia();
        return ResponseEntity.status(HttpStatus.OK).body(mainPageMediaResponseDtoList);
    }

    @GetMapping("/clubs")
    public ResponseEntity<List<MainPageClubResponseDto>> getMainPageClubs(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MainPageClubResponseDto> mainPageClubResponseDtos = mainService.getMainPageClubs(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(mainPageClubResponseDtos);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadClubMedia(@RequestBody List<MainMediaUploadRequestDto> mainMediaUploadRequestDtoList) {
        mainService.uploadMainMedia(mainMediaUploadRequestDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
