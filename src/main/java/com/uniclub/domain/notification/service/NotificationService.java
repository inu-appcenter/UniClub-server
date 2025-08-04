package com.uniclub.domain.notification.service;

import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.entity.Notification;
import com.uniclub.domain.notification.repository.NotificationRepository;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    //알림 불러오기
    public List<NotificationResponseDto> getNotification(UserDetailsImpl user) {
        List<Notification> notificationList = notificationRepository.findByUserId(user.getUserId());
        List<NotificationResponseDto> notificationResponseDtoList = new ArrayList<>();
        for (Notification notification : notificationList) {
            notificationResponseDtoList.add(NotificationResponseDto.from(notification));
        }
        return notificationResponseDtoList;
    }
}
