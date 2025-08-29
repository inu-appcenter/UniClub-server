package com.uniclub.domain.notification.service;

import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.notification.controller.NotificationController;
import com.uniclub.domain.notification.dto.NotificationRequestDto;
import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.entity.NotificationType;
import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    //알림 불러오기
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotification(UserDetailsImpl userDetails) {
        List<Notification> notificationList = notificationRepository.findByUserId(userDetails.getUserId());
        List<NotificationResponseDto> notificationResponseDtoList = new ArrayList<>();
        for (Notification notification : notificationList) {
            notificationResponseDtoList.add(NotificationResponseDto.from(notification));
        }
        return notificationResponseDtoList;
    }


    //알림 생성
    public void registerNotification(NotificationRequestDto notificationRequestDto) {
        log.info("알림 생성 시작: 알림 타입={}", notificationRequestDto.getNotificationType());

        //알림 종류 String -> enum 변환
        NotificationType notificationType = EnumConverter.stringToEnum(notificationRequestDto.getNotificationType(), NotificationType.class, ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);


        List<User> users = userRepository.findByStudentIds(notificationRequestDto.getStudentIds());
        // 요청된 학번과 실제 조회된 유저 수 검증
        if (users.size() != notificationRequestDto.getStudentIds().size()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 알림 객체 생성 및 리스트로 변환
        List<Notification> notifications = users.stream()
                .map(user -> notificationRequestDto.toNotificationEntity(user, notificationType))
                .toList();

        // 알림 객체 DB에 저장
        notificationRepository.saveAll(notifications);

        log.info("알림 생성 성공: 알림 타입={}, 메시지={}, 수신자={}", notificationRequestDto.getNotificationType(), notificationRequestDto.getMessage(), notificationRequestDto.getStudentIds());
    }

    //알림 삭제
    public void deleteNotification(UserDetailsImpl userDetails, Long notificationId) {

        // 알림 존재 여부 확인
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        // 악의적인 요청, 다른 유저의 알림을 지우려 할 때
        if (!notification.getUser().getUserId().equals(userDetails.getUserId())) {
            log.warn("권한 없는 알림 삭제 요청: userId={}, notificationId={}, 실제 소유 userId={}",
                    userDetails.getUserId(), notificationId, notification.getUser().getUserId());
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }
        
        // 3. 삭제 실행
        notificationRepository.delete(notification);
        
        log.info("알림 삭제 완료: userId={}, notificationId={}", userDetails.getUserId(), notificationId);
    }

    //알림 읽음 처리
    public void markAsRead(UserDetailsImpl userDetails, Long notificationId) {

        // 알림 존재 여부 확인
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        // 권한 검증 (해당 유저의 알림인지)
        if (!notification.getUser().getUserId().equals(userDetails.getUserId())) {
            log.warn("권한 없는 알림 읽음 요청: userId={}, notificationId={}, 실제 소유 userId={}", 
                    userDetails.getUserId(), notificationId, notification.getUser().getUserId());
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }
        
        // 읽음 처리 (더티체킹)
        notification.markAsRead();
        
        log.info("알림 읽음 처리 완료: userId={}, notificationId={}", userDetails.getUserId(), notificationId);
    }
}
