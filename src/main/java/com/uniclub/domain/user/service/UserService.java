package com.uniclub.domain.user.service;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public void updateUser(UserDetailsImpl userDetails, InformationModificationRequestDto request) {
        String studentId = userDetails.getStudentId();

        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 영속성 컨택스트 이용(더티체킹)
        user.updateInfo(request.getName(), request.getMajor());
    }

    public void deleteUser(UserDetailsImpl userDetails) {
        String studentId = userDetails.getStudentId();

        // 유저 조회, 존재하지 않는 경우 예외처리
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
