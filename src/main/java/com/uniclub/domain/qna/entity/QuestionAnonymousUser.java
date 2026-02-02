package com.uniclub.domain.qna.entity;

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
@Table(name = "question_anonymous_user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "user_id"}))
public class QuestionAnonymousUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @Column(nullable = false)
    private Integer anonymousOrder;

    public static QuestionAnonymousUser create(Question question, User user, Integer anonymousOrder) {
        QuestionAnonymousUser entity = new QuestionAnonymousUser();
        entity.question = question;
        entity.user = user;
        entity.anonymousOrder = anonymousOrder;
        return entity;
    }
}
