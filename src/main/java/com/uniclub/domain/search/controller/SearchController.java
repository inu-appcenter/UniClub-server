package com.uniclub.domain.search.controller;

import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.search.service.SearchService;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<ClubResponseDto>> search(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam String keyword) {
        List<ClubResponseDto> clubResponseDtoList = searchService.search(user, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(clubResponseDtoList);
    }
}
