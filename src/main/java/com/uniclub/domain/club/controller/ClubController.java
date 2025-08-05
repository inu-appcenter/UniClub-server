package com.uniclub.domain.club.controller;

import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionResponseDto;
import com.uniclub.domain.club.dto.ClubResponseDto;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class ClubController implements ClubApiSpecification {

    private final ClubService clubService;

    @GetMapping("/clubs")
    public ResponseEntity<Slice<ClubResponseDto>> getClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String cursorName,
            @RequestParam(defaultValue = "10") int size
    ) {
        Slice<ClubResponseDto> clubResponseDtoList = clubService.getClubs(
                userDetails.getUser().getUserId(), category, sortBy, cursorName, size
        );
        return ResponseEntity.status(HttpStatus.OK).body(clubResponseDtoList);
    }


    @PostMapping("/{clubId}/favorite")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isNowFavorite = clubService.toggleFavorite(clubId, userDetails);
        String message = isNowFavorite ? "관심 동아리 등록 완료" : "관심 동아리 등록 취소 완료";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    @PostMapping
    public ResponseEntity<Void> createClub(@Valid @RequestBody ClubCreateRequestDto clubCreateRequestDto) {
        clubService.createClub(clubCreateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{clubId}")
    public ResponseEntity<Void> promotionRegister(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId, @RequestBody ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto) {
        clubService.saveClubPromotion(userDetails, clubId, clubPromotionRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/{clubId}")
    public ResponseEntity<ClubPromotionResponseDto> getClubPromotion(@PathVariable Long clubId) {
        ClubPromotionResponseDto clubPromotionResponseDto = clubService.getClubPromotion(clubId);
        return ResponseEntity.status(HttpStatus.OK).body(clubPromotionResponseDto);
    }

    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId) {
        clubService.deleteClub(userDetails, clubId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
