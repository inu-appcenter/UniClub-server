package com.uniclub.domain.auth.service;

import com.uniclub.domain.auth.dto.*;
import com.uniclub.domain.auth.repository.INUAuthRepository;
import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.user.entity.Major;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.JwtTokenProvider;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final INUAuthRepository inuAuthRepository;

    public void createUser(RegisterRequestDto registerRequestDto) {
        log.info("회원가입 시작: 학번={}", registerRequestDto.getStudentId());

        if (!registerRequestDto.isStudentVerification()) {   //재학생 인증 확인
            throw new CustomException(ErrorCode.STUDENT_VERIFICATION_REQUIRED);
        }

        if (!registerRequestDto.isPersonalInfoCollectionAgreement()){ // 개인정보 약관 동의 안한 경우 예외처리
            throw new CustomException(ErrorCode.NOT_AGREED);
        }

        if (userRepository.existsByStudentId(registerRequestDto.getStudentId())) { // 이미 가입된 유저가 있는 경우 예외처리
            throw new CustomException(ErrorCode.DUPLICATE_STUDENT_ID);
        }

        // String -> major 변환
        Major major = EnumConverter.stringToEnum(registerRequestDto.getMajor(), Major.class, ErrorCode.MAJOR_NOT_FOUND);

        User user = new User(
                registerRequestDto.getName(),
                registerRequestDto.getStudentId(),
                passwordEncoder.encode(registerRequestDto.getPassword()),
                major
        );

        userRepository.save(user);
        log.info("회원가입 성공: 학번={}, 이름={}", registerRequestDto.getStudentId(), registerRequestDto.getName());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("로그인 시작: 학번={}", loginRequestDto.getStudentId());
        //인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getStudentId(), loginRequestDto.getPassword());

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

        log.info("로그인 성공: 학번={}", userDetails.getUsername());
        return new LoginResponseDto(userId, token, expiresIn);
    }

    // 재학생 인증
    public StudentVerificationResponseDto studentVerification(StudentVerificationRequestDto studentVerificationRequestDto) {
        boolean verification = inuAuthRepository.verifySchoolLogin(studentVerificationRequestDto.getStudentId(), studentVerificationRequestDto.getPassword());
        log.info("재학생 인증: 학번={}, 성공여부={}", studentVerificationRequestDto.getStudentId(), verification);
        return new StudentVerificationResponseDto(verification);
    }


}
