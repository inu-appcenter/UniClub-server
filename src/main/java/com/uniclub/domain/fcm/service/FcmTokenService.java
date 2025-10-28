package com.uniclub.domain.fcm.service;

import com.uniclub.domain.fcm.dto.FcmRegisterRequestDto;
import com.uniclub.domain.fcm.entity.FcmToken;
import com.uniclub.domain.fcm.repository.FcmTokenRepository;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    //Fcm 토큰 저장
    public void registerFcmToken(UserDetailsImpl userDetails, FcmRegisterRequestDto fcmRegisterRequestDto) {
        //기존에 존재하는 토큰이면 삭제 (같은 기기의 다른 사용자인 경우 고려)
        fcmTokenRepository.deleteByToken(fcmRegisterRequestDto.getFcmToken());

        //새로 저장
        FcmToken fcmToken = FcmToken.builder()
                .token(fcmRegisterRequestDto.getFcmToken())
                .userId(userDetails.getUserId())
                .build();
        fcmTokenRepository.save(fcmToken);

        log.info("FCM 토큰 등록 완료: userId={}", userDetails.getUserId());
    }

    //사용자의 모든 Fcm 토큰 삭제
    public void unregisterFcmToken(UserDetailsImpl userDetails) {
        Long deletedCount = fcmTokenRepository.deleteAllByUserId(userDetails.getUserId());
        log.info("사용자 FCM 토큰 해제 완료: userId={}, 삭제된 토큰 개수={}", userDetails.getUserId(), deletedCount);
    }

}
