package com.uniclub.global.swagger;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.dto.ClubCreateRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionRegisterRequestDto;
import com.uniclub.domain.club.dto.ClubPromotionResponseDto;
import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "동아리 API")
public interface ClubApiSpecification {
    @Operation(summary = "동아리 조회", description = "전체, 카테고리별, 정렬순 조회")
    public ResponseEntity<Slice<ClubResponseDto>> getClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String cursorName,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "관심동아리 등록/취소")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "동아리 등록")
    public ResponseEntity<Void> createClub(
            @Valid @RequestBody ClubCreateRequestDto clubCreateRequestDto);

    @Operation(summary = "동아리 홍보페이지 작성 및 수정")
    public ResponseEntity<Void> promotionRegister(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long clubId,
            @RequestBody ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto);

    @Operation(summary = "동아리 홍보페이지 조회")
    public ResponseEntity<ClubPromotionResponseDto> getClubPromotion(@PathVariable Long clubId);

    @Operation(summary = "동아리 삭제")
    public ResponseEntity<Void> deleteClub(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long clubId);
}
