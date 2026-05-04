package com.uniclub.domain.fcm.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import com.uniclub.domain.fcm.dto.FcmMessageDto;
import com.uniclub.domain.fcm.dto.FcmResponseDto;
import com.uniclub.domain.fcm.repository.FcmTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    // 개별 토큰당 FCM 전송 최대 대기 시간 (SDK timeout의 fallback 역할)
    // SDK readTimeout보다 약간 길게 설정하여 SDK timeout이 우선 작동
    private static final long SEND_TIMEOUT_SECONDS = 15L;

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

        //각 토큰에 개별 전송 (토큰별 예외 격리)
        for (String token : fcmMessageDto.getTokens()) {
            SendStatus status = sendToToken(token, fcmMessageDto.getTitle(), fcmMessageDto.getContent());

            switch (status) {
                case SUCCESS -> successCount++;
                case FAILURE_RETRYABLE -> failureCount++;
                case FAILURE_INVALID_TOKEN -> {
                    failureCount++;
                    invalidTokens.add(token);
                }
            }

            //인터럽트 신호 시 루프 중단
            if (Thread.currentThread().isInterrupted()) {
                log.warn("FCM 전송 인터럽트 감지, 루프 중단");
                break;
            }
        }

        //유효하지 않은 토큰 삭제
        if (!invalidTokens.isEmpty()) {
            deleteInvalidTokensSync(invalidTokens);
        }

        log.info("FCM 전송 완료 - 성공: {}건, 실패: {}건", successCount, failureCount);

        return CompletableFuture.completedFuture(
                FcmResponseDto.builder()
                        .successCount(successCount)
                        .failureCount(failureCount)
                        .build()
        );
    }

    /**
     * 단일 토큰에 FCM 전송
     * 모든 예외를 흡수하여 루프 중단을 방지하고, 결과 상태만 반환
     */
    private SendStatus sendToToken(String token, String title, String content) {
        ApiFuture<String> future = null;
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .build();

            //비동기 전송 + 강제 타임아웃
            future = FirebaseMessaging.getInstance().sendAsync(message);
            String messageId = future.get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.debug("FCM 전송 성공: messageId={}", messageId);
            return SendStatus.SUCCESS;

        } catch (TimeoutException e) {
            future.cancel(true);
            log.warn("FCM 전송 타임아웃: token={}, timeout={}s", maskToken(token), SEND_TIMEOUT_SECONDS);
            return SendStatus.FAILURE_RETRYABLE;

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirebaseMessagingException fme) {
                log.error("FCM 전송 실패: token={}, errorCode={}, message={}", maskToken(token), fme.getMessagingErrorCode(), fme.getMessage());
                return isInvalidToken(fme) ? SendStatus.FAILURE_INVALID_TOKEN : SendStatus.FAILURE_RETRYABLE;
            }
            log.error("FCM 전송 중 예외: token={}, cause={}", maskToken(token), cause != null ? cause.getMessage() : "unknown");
            return SendStatus.FAILURE_RETRYABLE;

        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            log.warn("FCM 전송 인터럽트: token={}", maskToken(token));
            return SendStatus.FAILURE_RETRYABLE;

        } catch (RuntimeException e) {
            //예상치 못한 런타임 예외도 토큰별로 격리하여 루프 중단 방지
            log.error("FCM 전송 중 예상치 못한 오류: token={}, message={}", maskToken(token), e.getMessage(), e);
            return SendStatus.FAILURE_RETRYABLE;
        }
    }

    @Transactional
    public void deleteInvalidTokensSync(List<String> tokens) {
        for (String token : tokens) {
            fcmTokenRepository.deleteByToken(token);
        }
        log.info("유효하지 않은 FCM 토큰 삭제 완료: {}개", tokens.size());
    }

    //유효하지 않은 토큰인지 확인
    private boolean isInvalidToken(FirebaseMessagingException e) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();
        return errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT;
    }

    //토큰 마스킹 (로그용)
    private String maskToken(String token) {
        return token.substring(0, Math.min(20, token.length())) + "...";
    }

    /**
     * FCM 단일 토큰 전송 결과
     * - SUCCESS: 전송 성공
     * - FAILURE_RETRYABLE: 일시적 실패 (네트워크/타임아웃/일반 오류 - 다음에 재시도 가능)
     * - FAILURE_INVALID_TOKEN: 토큰 자체 문제 (만료/등록취소 - DB에서 삭제 대상)
     */
    private enum SendStatus {
        SUCCESS,
        FAILURE_RETRYABLE,
        FAILURE_INVALID_TOKEN
    }
}