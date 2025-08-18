package com.uniclub.domain.user.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.user.dto.InformationModificationRequestDto;
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

        // 영속성 컨택스트 이용(더티체킹)
        user.updateInfo(informationModificationRequestDto.getName(), informationModificationRequestDto.getMajor());
        log.info("사용자 정보 업데이트 성공: 학번={}", user.getStudentId());
    }

    public void deleteUser(UserDetailsImpl userDetails) {
        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(userDetails.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
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
}
