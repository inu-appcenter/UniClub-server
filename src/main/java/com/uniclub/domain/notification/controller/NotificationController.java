package com.uniclub.domain.notification.controller;

import com.uniclub.domain.notification.dto.NotificationCreateRequestDto;
import com.uniclub.domain.notification.dto.NotificationPageResponseDto;
import com.uniclub.domain.notification.service.NotificationService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.NotificationApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationApiSpecification {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationCreateRequestDto notificationCreateRequestDto){
        notificationService.createNotification(notificationCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<NotificationPageResponseDto> getNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Boolean isRead
    ) {
        NotificationPageResponseDto notificationPageResponseDto = notificationService.getNotifications(userDetails, pageable, isRead);
        return ResponseEntity.status(HttpStatus.OK).body(notificationPageResponseDto);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long notificationId) {
        notificationService.markAsRead(userDetails, notificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAsReadAll(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.markAsReadAll(userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long notificationId) {
        notificationService.deleteNotification(userDetails, notificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllReadNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.deleteAllReadNotifications(userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
