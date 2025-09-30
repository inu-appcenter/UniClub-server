package com.uniclub.domain.notification.service;

import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;









    //알림 읽음 처리
    public void markAsRead(UserDetailsImpl userDetails, Long notificationId) {
        //입력값 확인
        Notification notification = findNotificationAndValidateOwner(userDetails.getUserId(), notificationId);

        //읽음 처리
        notification.markAsRead();
    }





    //알림 조회 (소유주 확인 포함)
    private Notification findNotificationAndValidateOwner(Long userId, Long notificationId) {
        return notificationRepository.findByNotificationIdAndUserId(notificationId, userId).orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    //알림 존재 여부
    private void isNotificationExists(Long notificationId) {
        boolean notification = notificationRepository.existsById(notificationId);
        if (!notification) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }


}
