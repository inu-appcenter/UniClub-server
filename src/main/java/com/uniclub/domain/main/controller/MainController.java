package com.uniclub.domain.main.controller;

import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.main.service.MainService;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {
    private final MainService mainService;

    @GetMapping("/clubs")
    public ResponseEntity<List<MainPageClubResponseDto>> getMainPageClubs(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MainPageClubResponseDto> mainPageClubResponseDtos = mainService.getMainPageClubs(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(mainPageClubResponseDtos);
    }
}
