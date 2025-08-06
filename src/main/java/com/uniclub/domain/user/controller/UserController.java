package com.uniclub.domain.user.controller;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.domain.user.service.UserService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.SearchApiSpecification;
import com.uniclub.global.swagger.UserApiSpecification;
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
public class UserController implements UserApiSpecification {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto informationModificationRequestDto
    ) {
        userService.updateUser(userDetails, informationModificationRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
