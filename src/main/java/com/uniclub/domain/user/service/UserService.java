package com.uniclub.domain.user.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.entity.MediaType;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.terms.dto.RegisterTermsRequestDto;
import com.uniclub.domain.terms.entity.Terms;
import com.uniclub.domain.terms.repository.TermsRepository;
import jakarta.servlet.http.HttpServletRequest;
import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.dto.MyPageResponseDto;
import com.uniclub.domain.user.dto.NotificationSettingResponseDto;
import com.uniclub.domain.user.dto.ToggleNotificationResponseDto;
import com.uniclub.domain.user.dto.UserDeleteRequestDto;
import com.uniclub.domain.user.entity.Major;
import com.uniclub.global.s3.S3ServiceImpl;
import com.uniclub.domain.user.dto.UserRoleRequestDto;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final MediaRepository mediaRepository;
    private final TermsRepository termsRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3ServiceImpl s3ServiceImpl;

    public void updateUser(UserDetailsImpl userDetails, InformationModificationRequestDto informationModificationRequestDto) {
        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(userDetails.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제된 유저인지 확인
        if (user.isDeleted()){
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        // 프로필 이미지가 제공된 경우 Media 엔티티로 저장
        Media profileMedia = null;
        String profileImageLink = informationModificationRequestDto.getProfileImageLink();
        if (profileImageLink != null && !profileImageLink.isBlank()) {
            profileMedia = Media.builder()
                    .mediaLink(profileImageLink)
                    .mediaType(MediaType.USER_PROFILE)
                    .mainMedia(false)
                    .build();
            mediaRepository.save(profileMedia);
        }

        // String -> major 변환
        Major major = EnumConverter.stringToEnum(informationModificationRequestDto.getMajor(), Major.class, ErrorCode.MAJOR_NOT_FOUND);

        // 영속성 컨택스트 이용(더티체킹)
        user.updateInfo(informationModificationRequestDto.getName(),
                major,
                informationModificationRequestDto.getNickname(),
                profileMedia);


        log.info("사용자 정보 업데이트 성공: 학번={}", user.getStudentId());
    }


    public void deleteUser(UserDetailsImpl userDetails, UserDeleteRequestDto userDeleteRequestDto) {

        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(userDetails.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제된 유저인지 확인
        if (user.isDeleted()){
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        // 비밀번호 확인
        boolean isValid = passwordEncoder.matches(
                userDeleteRequestDto.getPassword(),
                userDetails.getPassword()
        );
        
        if (!isValid) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        user.softDelete();
        log.info("사용자 삭제 완료: 학번={}", user.getStudentId());
    }


    // 유저 권한부여 테스트용 API
    public void addRole(UserRoleRequestDto userRoleRequestDto) {
        User user = userRepository.findByName(userRoleRequestDto.getUserName()).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Club club = clubRepository.findByName(userRoleRequestDto.getClubName()).
                orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        Role role = EnumConverter.stringToEnum(userRoleRequestDto.getRole(), Role.class, ErrorCode.ROLE_NOT_FOUND);

        membershipRepository.save(MemberShip.builder().user(user).club(club).role(role).build());
        log.info("사용자 권한 부여 완료: 학번={}, 권한={}", user.getStudentId(), role);
    }

    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPage(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        String formattedStudentId = formatStudentId(user.getStudentId());

        // 프로필 이미지가 있으면 presigned URL 생성
        String profileImageUrl = "";
        if (user.getProfileMedia() != null) {
           profileImageUrl = s3ServiceImpl.getDownloadPresignedUrl(user.getProfileMedia().getMediaLink());
        }

        log.info("마이페이지 조회 완료: 학번={}", user.getStudentId());
        return new MyPageResponseDto(user.getNickname(), user.getName(), formattedStudentId, user.getMajor(), profileImageUrl);
    }

    // 학번 추출 private 메소드
    private String formatStudentId(String studentId) {

        // 실제 운영때는 수정 필요
        if (studentId != null && studentId.length() >= 4) {
            return studentId.substring(2, 4) + "학번";
        }
        return studentId + "학번";
    }

    @Transactional(readOnly = true)
    public NotificationSettingResponseDto getNotificationSetting(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        log.info("알림설정 조회 완료: 학번={}, 상태={}", user.getStudentId(), user.isNotificationEnabled());
        return new NotificationSettingResponseDto(user.isNotificationEnabled());
    }

    public ToggleNotificationResponseDto toggleNotificationSetting(UserDetailsImpl userDetails) {
        String studentId = userDetails.getStudentId();
        
        // DB에서 다시 조회하여 현재 트랜잭션 컨텍스트에 포함시키기
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제된 유저인지 확인
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        // 토글 실행 (더티체킹으로 자동 UPDATE)
        user.toggleNotification();
        
        if (user.isNotificationEnabled()) {
            log.info("알림설정 변경 완료: 학번={}, 상태=활성화", studentId);
            return new ToggleNotificationResponseDto("알림이 활성화되었습니다.");
        } else {
            log.info("알림설정 변경 완료: 학번={}, 상태=비활성화", studentId);
            return new ToggleNotificationResponseDto("알림이 비활성화되었습니다.");
         }
    }

    public void registerTerms(RegisterTermsRequestDto registerTermsRequestDto, HttpServletRequest request) {
        User user = userRepository.findByStudentId(registerTermsRequestDto.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        if (userAgent == null) {
            userAgent = "Unknown";
        }

        Terms terms = Terms.builder()
                .personalInfoCollectionAgreement(registerTermsRequestDto.isPersonalInfoCollectionAgreement())
                .marketingAdvertisement(registerTermsRequestDto.isMarketingAdvertisement())
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

        // 프록시를 거쳐서 들어오는 경우 진짜 클라이언트 IP 주소를 찾음
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
}
