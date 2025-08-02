package com.uniclub.domain.auth.service;

import com.uniclub.domain.auth.dto.LoginRequestDto;
import com.uniclub.domain.auth.dto.LoginResponseDto;
import com.uniclub.domain.auth.dto.RegisterRequestDto;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.JwtTokenProvider;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void createUser(RegisterRequestDto request) {

        if (!request.isAgreed()){ // 개인정보 약관 동의 안한 경우 예외처리
            throw new CustomException(ErrorCode.NOT_AGREED);
        }

        if (userRepository.existsByStudentId(request.getStudentId())) { // 이미 가입된 유저가 있는 경우 예외처리
            throw new CustomException(ErrorCode.DUPLICATE_STUDENT_ID);
        }

        User user = new User(
                request.getName(),
                request.getStudentId(),
                passwordEncoder.encode(request.getPassword()),
                request.getMajor()
        );

        userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        // 존재하지 않는 유저 정보일 때 예외처리, authentication에서 유저 정보를 추출하면 DB 조회 더 안해도 되지만 에러 핸들링을 위함
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //사용자 인증 & authentication 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getStudentId(), request.getPassword())
        );

        // jwt 토큰 생성
        String token = jwtTokenProvider.createToken(authentication);

        // 토큰 유효기간 (초 단위로 변환)
        Long expiresIn = jwtTokenProvider.getTokenValidityInMilliseconds() / 1000;

        return new LoginResponseDto(user.getUserId(), token, expiresIn);
    }


}
