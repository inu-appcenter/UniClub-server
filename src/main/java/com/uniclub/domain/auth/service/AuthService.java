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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        System.out.println("Login 요청 들어옴: " + request.getStudentId());
        //인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getStudentId(), request.getPassword());

        //인증 메니저로 인증
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //인증 성공 시 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Jwt 토큰 생성
        String token = jwtTokenProvider.createToken(authentication);

        //토큰 유효기간 (초 단위로 변환)
        Long expiresIn = jwtTokenProvider.getTokenValidityInMilliseconds() / 1000;

        // 인증된 사용자 정보 추출
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        return new LoginResponseDto(userId, token, expiresIn);
    }

}
