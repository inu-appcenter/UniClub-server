package com.uniclub.domain.terms.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false)
    private boolean personalInfoCollectionAgreement;

    @Column(nullable = false)
    private boolean marketingAdvertisement;

    @Column(nullable = false, length = 20)
    private String version; //약관 버전

    @Column(nullable = false, length = 20)
    private String ipAddress;   //동의한 IP주소

    @Column(nullable = false)
    private String userAgent;   //브라우저 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    private User user;

    @Builder
    public Terms(boolean personalInfoCollectionAgreement, boolean marketingAdvertisement, 
                String version, String ipAddress, String userAgent, User user) {
        this.personalInfoCollectionAgreement = personalInfoCollectionAgreement;
        this.marketingAdvertisement = marketingAdvertisement;
        this.version = version;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.user = user;
    }
}
