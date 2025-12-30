package com.uniclub.domain.notification.service;

import com.uniclub.domain.notification.dto.NotificationCreateRequestDto;
import com.uniclub.domain.notification.dto.NotificationPageResponseDto;
import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    //알림 생성(테스트)
    public void createNotification(NotificationCreateRequestDto notificationCreateRequestDto) {
        NotificationType notificationType = null;
        if (notificationCreateRequestDto.getNotificationType() != null && !notificationCreateRequestDto.getNotificationType().isBlank()){
            notificationType = EnumConverter.stringToEnum(notificationCreateRequestDto.getNotificationType(), NotificationType.class, ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
        }
        Notification notification = notificationCreateRequestDto.toEntity(notificationType);
        notificationRepository.save(notification);
        log.info("Notification created");
    }

    //알림 조회
    public NotificationPageResponseDto getNotifications(UserDetailsImpl userDetails, Pageable pageable, Boolean isRead) {
        Page<Notification> notifications;
        if (isRead == null) { // 모든 알림 조회
            notifications = notificationRepository.findByUserId(userDetails.getUserId(), pageable);
        } else { // 읽음/안읽음 필터링
            notifications = notificationRepository.findByUserIdAndRead(userDetails.getUserId(), isRead, pageable);
        }
        Page<NotificationResponseDto> notificationDtoPage= notifications.map(NotificationResponseDto::from);
        return NotificationPageResponseDto.from(notificationDtoPage);
    }


    //알림 읽음 처리
    public void markAsRead(UserDetailsImpl userDetails, Long notificationId) {
        Notification notification = findNotificationAndValidateOwner(userDetails.getUserId(), notificationId);
        notification.markAsRead();
    }

    //알림 전체 읽음 처리
    public void markAsReadAll(UserDetailsImpl userDetails) {
        notificationRepository.markAllAsReadByUserId(userDetails.getUserId());
    }


    //알림 삭제
    public void deleteNotification(UserDetailsImpl userDetails, Long notificationId) {
        Notification notification = findNotificationAndValidateOwner(userDetails.getUserId(), notificationId);
        notificationRepository.delete(notification);
    }

    //읽은 알림 전체 삭제
    public void deleteAllReadNotifications(UserDetailsImpl userDetails) {
        notificationRepository.deleteAllByUserIdAndReadTrue(userDetails.getUserId());
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
