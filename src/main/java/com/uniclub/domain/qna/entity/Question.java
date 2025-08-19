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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question")
public class Question extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false, length = 50)
    private String title;

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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")
    private Club club;

    @Builder
    public Question(String title, String content, boolean isAnonymous, boolean isPublic, User user, Club club) {
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isPublic = isPublic;
        this.user = user;
        this.club = club;
    }

    public Question update(Question question) {
        this.title = question.getTitle();
        this.content = question.getContent();
        this.isAnonymous = question.isAnonymous();
        this.isPublic = question.isPublic();
        this.user = question.getUser();
        this.club = question.getClub();
        return this;
    }
}
