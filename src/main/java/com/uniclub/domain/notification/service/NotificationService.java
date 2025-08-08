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
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void registerNotification(NotificationRequestDto notificationRequestDto) {
        System.out.println(notificationRequestDto.getNotificationType());
        //알림 종류 String -> enum 변환
        NotificationType notificationType = NotificationType.from(notificationRequestDto.getNotificationType());

        // 알림을 받아야 할 유저 리스트 조회
        List<User> users = userRepository.findByUsernames(notificationRequestDto.getUserNames());

        // 알림 객체 생성 및 리스트로 변환
        List<Notification> notifications = users.stream()
                .map(user -> notificationRequestDto.toNotificationEntity(user, notificationType))
                .toList();

        // 알림 객체 DB에 저장
        notificationRepository.saveAll(notifications);

    }
}
