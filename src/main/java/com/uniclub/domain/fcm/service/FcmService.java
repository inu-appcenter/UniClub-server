package com.uniclub.domain.fcm.service;

import com.google.firebase.messaging.*;
import com.uniclub.domain.fcm.dto.FcmMessageDto;
import com.uniclub.domain.fcm.dto.FcmResponseDto;
import com.uniclub.domain.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * FCM 알림 전송 (HTTP v1 API 사용)
     * sendMulticast 대신 개별 send 사용
     */
    @Async("sendExecutor")
    public CompletableFuture<FcmResponseDto> sendNotificationAsync(FcmMessageDto fcmMessageDto) {

        //빈 토큰 리스트 체크
        if (fcmMessageDto.getTokens().isEmpty()) {
            log.warn("전송할 FCM 토큰이 없습니다.");
            return CompletableFuture.completedFuture(
                    FcmResponseDto.builder()
                            .successCount(0)
                            .failureCount(0)
                            .build()
            );
        }

        log.info("FCM 전송 시작: 토큰 개수={}, title={}",
                fcmMessageDto.getTokens().size(), fcmMessageDto.getTitle());

        int successCount = 0;
        int failureCount = 0;
        List<String> invalidTokens = new ArrayList<>();

        //각 토큰에 개별 전송
        for (String token : fcmMessageDto.getTokens()) {
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder()
                                .setTitle(fcmMessageDto.getTitle())
                                .setBody(fcmMessageDto.getContent())
                                .build())
                        .build();

                //개별 전송
                String messageId = FirebaseMessaging.getInstance().send(message);
                successCount++;
                log.debug("FCM 전송 성공: messageId={}", messageId);

            } catch (FirebaseMessagingException e) {
                failureCount++;
                log.error("FCM 전송 실패: token={}, errorCode={}, message={}",
                        token.substring(0, Math.min(20, token.length())) + "...",
                        e.getMessagingErrorCode(),
                        e.getMessage());

                // 유효하지 않은 토큰 수집
                if (isInvalidToken(e)) {
                    invalidTokens.add(token);
                }
            }
        }

        // 유효하지 않은 토큰 삭제
        if (!invalidTokens.isEmpty()) {
            try {
                for (String invalidToken : invalidTokens) {
                    fcmTokenRepository.deleteByToken(invalidToken);
                }
                log.info("유효하지 않은 FCM 토큰 삭제 완료: {}개", invalidTokens.size());
            } catch (Exception e) {
                log.error("FCM 토큰 삭제 중 오류 발생", e);
            }
        }

        log.info("FCM 전송 완료 - 성공: {}건, 실패: {}건", successCount, failureCount);

        return CompletableFuture.completedFuture(
                FcmResponseDto.builder()
                        .successCount(successCount)
                        .failureCount(failureCount)
                        .build()
        );
    }

    //유효하지 않은 토큰인지 확인
    private boolean isInvalidToken(FirebaseMessagingException e) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();
        return errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT;
    }


}
