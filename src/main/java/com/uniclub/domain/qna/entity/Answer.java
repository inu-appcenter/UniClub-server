package com.uniclub.domain.qna.entity;

import com.uniclub.domain.user.entity.User;
import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "answer")
public class Answer extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean anonymous;

    @Column
    private Integer anonymousOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentAnswerId")
    private Answer parentAnswer;

    @Builder
    public Answer(String content, boolean anonymous, Integer anonymousOrder, Question question, User user, Answer parentAnswer) {
        this.content = content;
        this.anonymous = anonymous;
        this.anonymousOrder = anonymousOrder;
        this.question = question;
        this.user = user;
        this.parentAnswer = parentAnswer;
    }

    public void assignAnonymousOrderIfNull(Integer order) {
        if (this.anonymousOrder == null) {
            this.anonymousOrder = order;
        }
    }
}
