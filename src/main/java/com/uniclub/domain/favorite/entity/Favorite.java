package com.uniclub.domain.favorite.entity;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteId;    //PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")    //FK
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")    //FK
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Club club;

    public Favorite(User user, Club club) {
        this.user = user;
        this.club = club;
    }
}
