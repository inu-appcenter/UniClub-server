package com.uniclub.domain.club.controller;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionResponseDto;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.service.ClubService;
import com.uniclub.domain.user.entity.User;
import com.uniclub.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<List<ClubResponseDto>> getAllClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ClubResponseDto> result = clubService.getAllClubs(userDetails.getUser());
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category")
    public ResponseEntity<List<ClubResponseDto>> getClubsByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String category) {
        CategoryType categoryType = CategoryType.valueOf(category);
        List<ClubResponseDto> result = clubService.getClubsByCategory(userDetails.getUser(), categoryType);
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{clubId}/favorite")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isNowFavorite = clubService.toggleFavorite(clubId, userDetails.getUser());
        String message = isNowFavorite ? "관심 동아리 등록 완료" : "관심 동아리 등록 취소 완료";
        return ResponseEntity.ok(message);
    }


    @PostMapping
    public ResponseEntity<Void> createClub(@Valid @RequestBody ClubCreateRequestDto clubCreateRequestDto) {
        clubService.createClub(clubCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/{clubId}")
    public ResponseEntity<Void> promotionRegister(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long clubId, @RequestBody ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto) {
        clubService.saveClubPromotion(user, clubId, clubPromotionRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @GetMapping("/{clubId}")
    private ResponseEntity<ClubPromotionResponseDto> getClubPromotion(@PathVariable Long clubId) {
        ClubPromotionResponseDto clubPromotionResponseDto = clubService.getClubPromotion(clubId);
        return ResponseEntity.status(HttpStatus.OK).body(clubPromotionResponseDto);
    }

    @DeleteMapping("/{clubId}")
    private ResponseEntity<Void> deleteClub(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long clubId) {
        clubService.deleteClub(user, clubId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
