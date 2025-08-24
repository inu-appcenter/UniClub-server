package com.uniclub.domain.auth.controller;

import com.uniclub.domain.auth.dto.*;
import com.uniclub.domain.auth.service.AuthService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.AuthApiSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiSpecification {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegisterRequestDto request) {
        authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/register/student-verification")
    public ResponseEntity<StudentVerificationResponseDto> studentVerification(@Valid @RequestBody StudentVerificationRequestDto studentVerificationRequestDto) {
        StudentVerificationResponseDto studentVerificationResponseDto = authService.studentVerification(studentVerificationRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(studentVerificationResponseDto);
    }

}
