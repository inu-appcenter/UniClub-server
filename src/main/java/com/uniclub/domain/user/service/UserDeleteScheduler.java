package com.uniclub.domain.user.service;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UserDeleteScheduler {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteUsers() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
        List<User> usersToDelete = userRepository.findByDeletedTrueAndDeletedAtBefore(cutoffDate);
        log.info("{}개월 지난 softDelete 유저 정리 시작: {}명", cutoffDate, usersToDelete.size());

        if (usersToDelete.isEmpty()) {
            log.info("정리할 삭제 유저가 없습니다.");
            return;
        }

        int s3DeletedCount = 0;
        int s3FailedCount = 0;

        //S3 이미지 삭제
        for (User user : usersToDelete) {
            String profileMedia = user.getProfile();
            if (profileMedia != null) {
                try {
                    s3Service.deleteFile(profileMedia);
                    s3DeletedCount++;
                    log.info("S3 프로필 이미지 삭제 성공: userId={}, studentId={}, profile={}", user.getUserId(), user.getStudentId(), profileMedia);
                } catch (Exception e) {
                    s3FailedCount++;
                    log.error("S3 프로필 이미지 삭제 실패 (계속 진행): userId={}, studentId={}, profile={}, error={}", user.getUserId(), user.getStudentId(), profileMedia, e.getMessage());
                }
            }
        }

        //DB 이미지 삭제
        List<Long> userIds = usersToDelete.stream()
                .map(User::getUserId)
                .toList();

        //알림 삭제
        notificationRepository.deleteByUserIds(userIds);
        log.info("유저 알림 삭제 완료: {}개 유저", userIds.size());


        //User 물리 삭제
        userRepository.deleteAll(usersToDelete);

        log.info("====== 유저 정리 완료 ======");
        log.info("물리 삭제된 유저: {}명", usersToDelete.size());
        log.info("S3 파일 삭제 성공: {}개", s3DeletedCount);
        log.info("S3 파일 삭제 실패: {}개", s3FailedCount);
        log.info("===========================");
    }

}
