package com.uniclub.domain.terms.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
public class Terms extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termsId;    //PK

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; //약관 내용

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean agreed; //동의 여부

    @Column(nullable = false, length = 20)
    private String version; //약관 버전

    @Column(nullable = false, length = 20)
    private String ipAddress;   //동의한 IP주소

    @Column(nullable = false, length = 30)
    private String userAgent;   //브라우저 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    private User user;

}
