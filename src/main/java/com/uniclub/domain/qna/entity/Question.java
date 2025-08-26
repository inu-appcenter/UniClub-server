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
    private boolean anonymous;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean answered;

    @Column(nullable = false)
    private boolean publicQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Club club;

    @Builder
    public Question(String content, boolean anonymous, boolean answered, boolean publicQuestion, User user, Club club) {
        this.content = content;
        this.anonymous = anonymous;
        this.answered = answered;
        this.publicQuestion = publicQuestion;
        this.user = user;
        this.club = club;
    }

    public void update(String content, Boolean anonymous, Boolean answered) {
        if (content != null) {
            this.content = content;
        }
        if (anonymous != null) {
            this.anonymous = anonymous;
        }
        if (answered != null) {
            this.answered = answered;
        }
    }
}
