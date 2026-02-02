package com.uniclub.domain.qna.repository;

import com.uniclub.domain.qna.entity.QuestionAnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnonymousUserRepository extends JpaRepository<QuestionAnonymousUser, Long> {

    @Query("SELECT qau FROM QuestionAnonymousUser qau " +
            "LEFT JOIN FETCH qau.user " +
            "WHERE qau.question.questionId = :questionId")
    List<QuestionAnonymousUser> findAllByQuestionId(@Param("questionId") Long questionId);

    boolean existsByQuestionQuestionIdAndUserUserId(Long questionId, Long userId);

    @Query("SELECT COALESCE(MAX(qau.anonymousOrder), 0) FROM QuestionAnonymousUser qau " +
            "WHERE qau.question.questionId = :questionId")
    Integer findMaxAnonymousOrderByQuestionId(@Param("questionId") Long questionId);
}
