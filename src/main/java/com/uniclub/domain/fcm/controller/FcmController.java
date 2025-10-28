package com.uniclub.domain.fcm.controller;

import com.uniclub.domain.fcm.dto.FcmRegisterRequestDto;
import com.uniclub.domain.fcm.service.FcmTokenService;
import com.uniclub.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerToken(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody FcmRegisterRequestDto fcmRegisterRequestDto) {
        fcmTokenService.registerFcmToken(userDetails, fcmRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteToken(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        fcmTokenService.unregisterFcmToken(userDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
