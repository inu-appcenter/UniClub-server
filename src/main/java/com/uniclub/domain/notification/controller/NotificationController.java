package com.uniclub.domain.notification.controller;

import com.uniclub.domain.notification.dto.NotificationRequestDto;
import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.domain.notification.service.NotificationService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.NotificationApiSpecification;
import jakarta.validation.Valid;
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


    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<NotificationResponseDto> notificationResponseDtoList = notificationService.getNotification(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(notificationResponseDtoList);
    }

    @PostMapping
    public ResponseEntity<Void> registerNotification(@Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        notificationService.registerNotification(notificationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
