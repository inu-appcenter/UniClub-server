package com.uniclub.domain.club.controller;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.service.ClubService;
import com.uniclub.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<List<ClubResponseDto>> getAllClubs(User user) {
        List<ClubResponseDto> result = clubService.getAllClubs(user);
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category")
    public ResponseEntity<List<ClubResponseDto>> getClubsByCategory(User user, @RequestParam String category) {
        try {
            CategoryType categoryType = CategoryType.valueOf(category);
            List<ClubResponseDto> result = clubService.getClubsByCategory(user, categoryType);
            if (result.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{clubId}/favorites")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long clubId, User user) {
        boolean isNowFavorite = clubService.toggleFavorite(clubId, user);
        String message = isNowFavorite ? "관심 동아리 등록 완료" : "관심 동아리 등록 취소 완료";
        return ResponseEntity.ok(message);
    }
}

