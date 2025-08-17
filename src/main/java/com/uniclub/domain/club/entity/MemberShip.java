package com.uniclub.domain.club.entity;


import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership")
public class MemberShip extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;    //PK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;    //권한 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")    //FK
    private Club club;

    @Builder
    public MemberShip(Role role, User user, Club club) {
        this.role = role;
        this.user = user;
        this.club = club;
    }
}
