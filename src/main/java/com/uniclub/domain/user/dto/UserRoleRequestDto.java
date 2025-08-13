package com.uniclub.domain.user.dto;

import com.uniclub.domain.club.entity.MemberShip;
import lombok.Getter;

@Getter
public class UserRoleRequestDto {
    private String userName;
    private String clubName;
    private String role;

}
