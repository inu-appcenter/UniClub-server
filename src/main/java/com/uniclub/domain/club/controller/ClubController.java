package com.uniclub.domain.club.controller;

import com.uniclub.domain.club.dto.*;
import com.uniclub.domain.club.service.ClubService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.ClubApiSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class ClubController implements ClubApiSpecification {

    private final ClubService clubService;

    @GetMapping("/clubs")
    public ResponseEntity<PageClubResponseDto<ClubResponseDto>> getClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String cursorName,
            @RequestParam(defaultValue = "10") int size
    ) {
        Slice<ClubResponseDto> slice = clubService.getClubs(
                userDetails.getUser().getUserId(), category, sortBy, cursorName, size
        );
        // 페이지 응답에 필요한 정보만 주기 위한 DTO로 변환
        PageClubResponseDto<ClubResponseDto> clubResponseDtoList = new PageClubResponseDto<>(slice.getContent(), slice.hasNext());
        return ResponseEntity.status(HttpStatus.OK).body(clubResponseDtoList);
    }


    @PostMapping("/{clubId}/favorite")
    public ResponseEntity<ToggleFavoriteResponseDto> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ToggleFavoriteResponseDto toggleFavoriteResponseDto = clubService.toggleFavorite(clubId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(toggleFavoriteResponseDto);
    }


    @PostMapping
    public ResponseEntity<Void> createClub(@Valid @RequestBody ClubCreateRequestDto clubCreateRequestDto) {
        clubService.createClub(clubCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{clubId}")
    public ResponseEntity<Void> promotionRegister(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId, @RequestBody ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto) {
        clubService.saveClubPromotion(userDetails, clubId, clubPromotionRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("{clubId}/upload")
    public ResponseEntity<Void> uploadClubMedia(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId, @RequestBody List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList) {
        clubService.uploadClubMedia(userDetails, clubId, clubMediaUploadRequestDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/{clubId}")
    public ResponseEntity<ClubPromotionResponseDto> getClubPromotion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId) {
        ClubPromotionResponseDto clubPromotionResponseDto = clubService.getClubPromotion(userDetails,clubId);
        return ResponseEntity.status(HttpStatus.OK).body(clubPromotionResponseDto);
    }

    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}
