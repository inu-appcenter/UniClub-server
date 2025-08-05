package com.uniclub.global.swagger;


import com.uniclub.domain.auth.dto.LoginRequestDto;
import com.uniclub.domain.auth.dto.LoginResponseDto;
import com.uniclub.domain.auth.dto.RegisterRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증 API")
public interface AuthApiSpecification {
    @Operation(summary = "회원가입")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegisterRequestDto request);

    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto);
}
