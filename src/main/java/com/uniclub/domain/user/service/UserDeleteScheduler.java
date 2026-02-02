package com.uniclub.domain.user.service;

import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
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

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteUsers() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
        List<User> usersToDelete = userRepository.findByDeletedTrueAndDeletedAtBefore(cutoffDate);
        log.info("6개월 지난 softDelete 유저 정리 시작: {}명", usersToDelete.size());

        if (usersToDelete.isEmpty()) {
            log.info("정리할 삭제 유저가 없습니다.");
            return;
        }

        List<Long> userIds = usersToDelete.stream()
                .map(User::getUserId)
                .toList();

        // 알림 삭제
        notificationRepository.deleteByUserIds(userIds);
        log.info("유저 알림 삭제 완료: {}개 유저", userIds.size());

        userRepository.deleteAll(usersToDelete);

        log.info("====== 유저 정리 완료 ======");
        log.info("물리 삭제된 유저: {}명", usersToDelete.size());
        log.info("===========================");
    }

}
