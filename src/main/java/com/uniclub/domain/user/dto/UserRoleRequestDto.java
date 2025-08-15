package com.uniclub.domain.user.dto;

import com.uniclub.domain.club.entity.MemberShip;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "유저 권한 부여 요청 DTO")
@Getter
public class UserRoleRequestDto {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    
    @Schema(description = "동아리 이름", example = "앱센터")
    private String clubName;
    
    @Schema(description = "부여할 권한", example = "ADMIN")
    private String role;
}
