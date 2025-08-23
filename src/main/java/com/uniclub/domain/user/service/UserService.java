package com.uniclub.domain.user.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.dto.MyPageResponseDto;
import com.uniclub.domain.user.dto.NotificationSettingResponseDto;
import com.uniclub.domain.user.dto.ToggleNotificationResponseDto;
import com.uniclub.domain.user.dto.UserRoleRequestDto;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 개인정보 수정 디자인 나오면 검증로직 추가 필요
    public void updateUser(UserDetailsImpl userDetails, InformationModificationRequestDto informationModificationRequestDto) {
        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(userDetails.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제된 유저인지 확인
        if (user.isDeleted()){
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        // 영속성 컨택스트 이용(더티체킹)
        user.updateInfo(informationModificationRequestDto.getName(), informationModificationRequestDto.getMajor(), informationModificationRequestDto.getNickname());
        log.info("사용자 정보 업데이트 성공: 학번={}", user.getStudentId());
    }

    public void deleteUser(UserDetailsImpl userDetails) {
        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(userDetails.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제된 유저인지 확인
        if (user.isDeleted()){
            throw new CustomException(ErrorCode.USER_DELETED);
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

        log.info("마이페이지 조회 완료: 학번={}", user.getStudentId());
        return new MyPageResponseDto(user.getName(), formattedStudentId, user.getMajor());
    }

    // 학번 추출 private 메소드
    private String formatStudentId(String studentId) {
        if (studentId != null && studentId.length() >= 2) {
            return studentId.substring(0, 2) + "학번";
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
}
