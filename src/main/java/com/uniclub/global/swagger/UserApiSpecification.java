package com.uniclub.global.swagger;

import com.uniclub.domain.user.dto.InformationModificationRequestDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 API")
public interface UserApiSpecification {
    @Operation(summary = "유저 개인정보 수정")
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody InformationModificationRequestDto informationModificationRequestDto
    );

    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails);
}
