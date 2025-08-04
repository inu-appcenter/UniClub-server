package com.uniclub.domain.notification.controller;

import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.service.NotificationService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.NotificationApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/notifications")
public class NotificationController implements NotificationApiSpecification {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<NotificationResponseDto> notificationResponseDtoList = notificationService.getNotification(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(notificationResponseDtoList);
    }
}
