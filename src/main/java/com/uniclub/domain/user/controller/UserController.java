package com.uniclub.domain.user.controller;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.service.UserService;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto request
    ) {
        userService.updateUser(userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
