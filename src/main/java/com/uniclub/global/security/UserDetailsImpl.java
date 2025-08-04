package com.uniclub.global.security;

import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    //해당 유저 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //유저 PK
    public Long getUserId() {
        return this.user.getUserId();
    }

    public String getStudentId() {
        return this.user.getStudentId();
    }

    //비밀번호
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    //이름
    @Override
    public String getUsername() {
        return this.user.getStudentId();
    }

    //계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    //계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    //비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    //사용자 활성화 여부
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // 일반 로그인용 빌더
    public static UserDetailsImpl of(User user) {
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        return new UserDetailsImpl(user, authorities);
    }
}
