package com.uniclub.domain.qna.entity;


import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question")
public class Question extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous;

    @ColumnDefault("false")
    private boolean isAnswered;

    @Column(nullable = false)
    private boolean isPublic;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Club club;

    @Builder
    public Question(String content, boolean isAnonymous, boolean isAnswered, boolean isPublic, User user, Club club) {
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isAnswered = isAnswered;
        this.isPublic = isPublic;
        this.user = user;
        this.club = club;
    }

    public void update(String content, boolean isAnonymous, boolean isAnswered) {
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isAnswered = isAnswered;
    }
}
