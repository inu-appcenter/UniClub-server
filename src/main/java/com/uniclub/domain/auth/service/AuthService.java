package com.uniclub.domain.auth.service;

import com.uniclub.domain.auth.dto.*;
import com.uniclub.domain.auth.repository.INUAuthRepository;
import com.uniclub.domain.terms.entity.Terms;
import com.uniclub.domain.terms.repository.TermsRepository;
import com.uniclub.domain.user.entity.Major;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.JwtTokenProvider;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final INUAuthRepository inuAuthRepository;
    private final TermsRepository termsRepository;

    public void createUser(RegisterRequestDto registerRequestDto, HttpServletRequest request) {
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

        User user = User.builder()
                .name(registerRequestDto.getName())
                .studentId(registerRequestDto.getStudentId())
                .major(major)
                .nickname(registerRequestDto.getNickname())
                .build();

        userRepository.save(user);
        saveTerms(registerRequestDto, user, request);
        log.info("회원가입 성공: 학번={}, 이름={}", registerRequestDto.getStudentId(), registerRequestDto.getName());
    }

    private void saveTerms(RegisterRequestDto dto, User user, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "Unknown";
        }

        Terms terms = Terms.builder()
                .personalInfoCollectionAgreement(dto.isPersonalInfoCollectionAgreement())
                .marketingAdvertisement(dto.isMarketingAdvertisement())
                .version("1.0")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .user(user)
                .build();

        termsRepository.save(terms);
        log.info("약관 동의 저장 완료: 학번={}, IP={}", user.getStudentId(), ipAddress);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("로그인 시작: 학번={}", loginRequestDto.getStudentId());

        //교내 DB로 인증 책임
        boolean verification = inuAuthRepository.verifySchoolLogin(loginRequestDto.getStudentId(), loginRequestDto.getPassword());

        if (!verification) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }

        //유저 정보 조회
        User user = userRepository.findByStudentId(loginRequestDto.getStudentId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //Entity 변환
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);

        //Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        //인증 성공 시 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Jwt 토큰 생성
        String token = jwtTokenProvider.createToken(authentication);

        //토큰 유효기간 (초 단위로 변환)
        Long expiresIn = jwtTokenProvider.getTokenValidityInMilliseconds() / 1000;

        // 인증된 사용자 정보 추출
        Long userId = userDetails.getUserId();

        log.info("로그인 성공: 학번={}", userDetails.getUsername());
        return new LoginResponseDto(userId, token, expiresIn);
    }

    // 재학생 인증
    public StudentVerificationResponseDto studentVerification(StudentVerificationRequestDto studentVerificationRequestDto) {
        boolean verification = inuAuthRepository.verifySchoolLogin(studentVerificationRequestDto.getStudentId(), studentVerificationRequestDto.getPassword());
        log.info("재학생 인증: 학번={}, 성공여부={}", studentVerificationRequestDto.getStudentId(), verification);

        if (userRepository.existsByStudentId(studentVerificationRequestDto.getStudentId())) { // 이미 가입된 유저가 있는 경우 예외처리
            throw new CustomException(ErrorCode.DUPLICATE_STUDENT_ID);
        }

        return new StudentVerificationResponseDto(verification);
    }


}
