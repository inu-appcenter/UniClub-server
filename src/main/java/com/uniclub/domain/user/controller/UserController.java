package com.uniclub.domain.user.controller;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.dto.MyPageResponseDto;
import com.uniclub.domain.user.dto.NotificationSettingResponseDto;
import com.uniclub.domain.user.dto.ToggleNotificationResponseDto;
import com.uniclub.domain.user.dto.UserDeleteRequestDto;
import com.uniclub.domain.user.dto.UserRoleRequestDto;
import com.uniclub.domain.user.service.UserService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.UserApiSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApiSpecification {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MyPageResponseDto myPageResponseDto = userService.getMyPage(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(myPageResponseDto);
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto informationModificationRequestDto
    ) {
        userService.updateUser(userDetails, informationModificationRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserDeleteRequestDto userDeleteRequestDto) {
        userService.deleteUser(userDetails, userDeleteRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/role")
    public ResponseEntity<Void> addRole(@Valid @RequestBody UserRoleRequestDto userRoleRequestDto){
        userService.addRole(userRoleRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/notification")
    public ResponseEntity<NotificationSettingResponseDto> getNotificationSetting(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        NotificationSettingResponseDto notificationSettingResponseDto = userService.getNotificationSetting(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(notificationSettingResponseDto);
    }

    @PatchMapping("/notification")
    public ResponseEntity<ToggleNotificationResponseDto> toggleNotificationSetting(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ToggleNotificationResponseDto toggleNotificationResponseDto = userService.toggleNotificationSetting(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(toggleNotificationResponseDto);
    }
}
