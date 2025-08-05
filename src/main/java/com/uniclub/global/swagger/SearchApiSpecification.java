package com.uniclub.global.swagger;

import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "검색 API")
public interface SearchApiSpecification {
    @Operation(summary = "동아리 검색 API", description = "사용자가 입력한 검색어로 동아리 검색")
    public ResponseEntity<List<ClubResponseDto>> search(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword);
}
