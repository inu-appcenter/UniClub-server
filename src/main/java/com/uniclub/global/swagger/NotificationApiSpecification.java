package com.uniclub.global.swagger;

import com.uniclub.domain.notification.dto.NotificationResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "알림 API")
public interface NotificationApiSpecification {
    @Operation(summary = "알림 리스트 조회")
    public ResponseEntity<List<NotificationResponseDto>> getNotification(@AuthenticationPrincipal UserDetailsImpl user);
}
