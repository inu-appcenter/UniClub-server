package com.uniclub.domain.fcm.service;

import com.google.firebase.messaging.*;
import com.uniclub.domain.fcm.dto.FcmMessageDto;
import com.uniclub.domain.fcm.dto.FcmResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    //FCM 알림 전송
    @Async("sendExecutor")
    public CompletableFuture<FcmResponseDto> sendNotificationAsync(FcmMessageDto fcmMessageDto) {
        FcmResponseDto result;

        try {
            //빈 토큰 리스트 체크
            if(fcmMessageDto.getTokens().isEmpty()) {
                log.warn("전송할 FCM 토큰이 없습니다.");
                result = FcmResponseDto.builder()
                        .successCount(0)
                        .failureCount(0)
                        .build();
            } else {
                //Firebase MulticastMessage 생성
                MulticastMessage message = MulticastMessage.builder()
                        .setNotification(Notification.builder()
                                .setTitle(fcmMessageDto.getTitle())
                                .setBody(fcmMessageDto.getContent())
                                .build())
                        .addAllTokens(fcmMessageDto.getTokens())
                        .build();

                //전송 실행
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

                log.info("FCM 전송 완료 - 성공: {}건, 실패: {}건", response.getSuccessCount(), response.getFailureCount());

                result = FcmResponseDto.builder()
                        .successCount(response.getSuccessCount())
                        .failureCount(response.getFailureCount())
                        .build();
            }

        } catch (FirebaseMessagingException e) {
            log.warn("FCM 전송 중 오류 발생: ", e);
            result = FcmResponseDto.builder()
                    .successCount(0)
                    .failureCount(fcmMessageDto.getTokens().size())
                    .build();
        }

        return CompletableFuture.completedFuture(result);
    }


}
