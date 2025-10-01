package com.uniclub.domain.notification.service;

import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.favorite.repository.FavoriteRepository;
import com.uniclub.domain.fcm.dto.FcmMessageDto;
import com.uniclub.domain.fcm.repository.FcmTokenRepository;
import com.uniclub.domain.fcm.service.FcmService;
import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import com.uniclub.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationEventProcessor {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FavoriteRepository favoriteRepository;
    private final MembershipRepository membershipRepository;
    private final FcmService fcmService;
    private final ClubRepository clubRepository;

    //동아리 모집 시작
    @Async("messageExecutor")
    public void clubRecruitmentStarted(Long clubId) {
        String clubName = getClubName(clubId);
        log.info("동아리 모집 시작 푸시 알림 전송 시작: clubId={}, clubName={}", clubId, clubName);

        try{
            List<Long> favoriteUserIds = getFavoriteUserIds(clubId);

            if (favoriteUserIds.isEmpty()) {
                log.info("관심 등록한 유저 없음: clubId={}, clubName={}", clubId, clubName);
                return;
            }

            String title = String.format("%s 모집 시작", clubName);
            String message = String.format("%s 동아리의 모집이 시작되었습니다!", clubName);

            processNotification(favoriteUserIds, title, message, NotificationType.CLUB, clubId);

        } catch (Exception e) {
            log.warn("동아리 모집 시작 푸시 알림 전송 중 오류: clubId={}, clubName={}", clubId, clubName);
        }
    }

    //동아리 모집 마감 임박 (7, 3, 1일 전)
    @Async("messageExecutor")
    public void clubRecruitmentDeadLine(Long clubId, int dayLeft) {
        String clubName = getClubName(clubId);
        log.info("모집 마감 입박 푸시 알림 전송 시작: clubId={}, clubName={}, dayLeft={}일", clubId, clubName, dayLeft);

        try {
            List<Long> favoritedUserIds = getFavoriteUserIds(clubId);

            if (favoritedUserIds.isEmpty()) {
                log.info("관심 등록한 유저 없음: clubId={}, clubName={}", clubId, clubName);
                return;
            }

            String title = String.format("%s 동아리 모집 마감 예정", clubName);
            String message = String.format("%s 동아리의 모집이 %d일 후 마감됩니다!", clubName, dayLeft);

            processNotification(favoritedUserIds, title, message, NotificationType.CLUB, clubId);

        } catch (Exception e) {
            log.warn("동아리 모집 마감 임막 푸시 알림 전송 중 오류: clubId={}, clubName={}, dayLeft={}일", clubId, clubName, dayLeft);
        }
    }

    //동아리 모집 마감 처리(사용 안 함)
    @Async("messageExecutor")
    public void clubRecruitmentEnded(Long clubId) {
        String clubName = getClubName(clubId);
        log.info("동아리 모집 마감 푸시 알림 전송 시작: clubId={}, clubName={}", clubId, clubName);

        try{
            List<Long> favoritedUserIds = getFavoriteUserIds(clubId);

            if (favoritedUserIds.isEmpty()) {
                log.info("관심 등록한 유저 없음: clubId={}, clubName={}", clubId, clubName);
                return;
            }

            String title = "UniClub";
            String message = String.format("%s 동아리의 모집이 마감되었습니다.", clubName);

            processNotification(favoritedUserIds, title, message, NotificationType.CLUB, clubId);

        } catch (Exception e) {
            log.warn("동아리 모집 마감 푸시 알림 전송 중 오류: clubId={}, clubName={}", clubId, clubName);
        }
    }

    //답변 등록
    @Async("messageExecutor")
    public void answerRegisterd(Long questionId, Long answerId, String content, Long questionerId) {
        log.info("답변 등록 푸시 알림 전송 시작: qustionId={}, answerId={}", questionId, answerId);

        try {
            String title = truncateContent(content, 15);
            String message = "질문에 새로운 답변이 등록되었습니다.";

            processNotification(List.of(questionerId), title, message, NotificationType.QNA, questionId);

        } catch (Exception e) {
            log.warn("답변 등록 푸시 알림 전송 중 오류: questionId={}, answerId={}", questionId, answerId);
        }
    }

    //질문 등록 (동아리 회장에게 알림)
    @Async("messageExecutor")
    public void questionRegistered(Long questionId, Long clubId) {
        String clubName = getClubName(clubId);
        log.info("질문 등록 푸시 알림 전송 시작: questionId={}, clubId={}, clubName={}", questionId, clubId, clubName);

        try {
            Long clubPresidentId = getClubPresidentId(clubId);

            if (clubPresidentId == null) {
                log.info("동아리 회장을 찾을 수 없음: clubId={}, clubName={}", clubId, clubName);
                return;
            }

            String title = "답변을 기다리고 있는 질문이 있어요!";
            String message = String.format("%s 동아리에 새로운 질문이 등록되었습니다.", clubName);

            processNotification(List.of(clubPresidentId), title, message, NotificationType.QNA, questionId);

        } catch (Exception e) {
            log.warn("질문 등록 푸시 알림 전송 중 오류: questionId={}", questionId, e);
        }
    }

    //대댓글 알림
    @Async("messageExecutor")
    public void replyRegistered(Long questionId, Long answererId, String content) {
        log.info("대댓글 등록 푸시 알림 전송 시작: questionId={}", questionId);

        try {
            String title = truncateContent(content, 15);
            String message = "새로운 대댓글이 등록되었습니다.";

            processNotification(List.of(answererId), title, message, NotificationType.QNA, questionId);

        } catch (Exception e) {
            log.warn("대댓글 알림 푸시 알림 전송 중 오류: questionId={}", questionId, e);
        }
    }

    //신고 제제 내역, 업데이트 요청



    //알림 저장 및 FCM 전송 통합 프로세스
    private void processNotification(List<Long> userIds, String title, String message, NotificationType notificationType, Long targetId) {

        //앱 내 알림 엔티티 생성
        saveNotifications(userIds, title, message, notificationType, targetId);

        //FCM 알림 전송
        sendPushNotifications(userIds, title, message);
    }


    //앱 내 엔티티 알림 생성
    private void saveNotifications(List<Long> userIds, String title, String message, NotificationType notificationType, Long targetId) {
        List<Notification> notifications = userIds.stream()
                .map(userId -> Notification.builder()
                        .title(title)
                        .message(message)
                        .notificationType(notificationType)
                        .targetId(targetId)
                        .userId(userId)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        log.info("앱 내 알림 저장 완료: {}건", notifications.size());
    }


    //FCM 푸시 알림 전송
    private void sendPushNotifications(List<Long> userIds, String title, String message) {
        List<String> fcmTokens = fcmTokenRepository.findTokensByUserIds(userIds);

        if(fcmTokens.isEmpty()) return;

        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .tokens(fcmTokens)
                .title(title)
                .content(message)
                .build();

        fcmService.sendNotificationAsync(fcmMessage);
    }

    //동아리 이름 추출
    private String getClubName(Long clubId) {
        return clubRepository.findNameByClubId(clubId);
    }

    //관심 동아리 설정한 회원 조회
    private List<Long> getFavoriteUserIds(Long clubId) {
        return favoriteRepository.findUserIdsByClubId(clubId);
    }

    //동아리 회장 조회
    private Long getClubPresidentId(Long clubId) {
        return membershipRepository.findUserIdByClubIdAndRole(clubId, Role.PRESIDENT)
                .orElseThrow(null);
    }

    //질문 내용 자르기
    private String truncateContent(String content, int maxLength) {
        String trimmed = content.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength) + "...";
    }


}
