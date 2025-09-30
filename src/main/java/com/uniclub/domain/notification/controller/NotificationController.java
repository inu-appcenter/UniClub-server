package com.uniclub.domain.notification.controller;

import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.service.NotificationService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.NotificationApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationApiSpecification {

    private final NotificationService notificationService;
/*
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnReadNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.getUnReadNotifications(userDetails);
    }

    @GetMapping("/read")
    public ResponseEntity<List<NotificationResponseDto>> getReadNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {

    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long notificationId) {
        notificationService.markAsRead(userDetails, notificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long notificationId) {
        notificationService.deleteNotification(userDetails, notificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

 */
}
