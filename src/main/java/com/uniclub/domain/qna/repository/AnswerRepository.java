package com.uniclub.domain.qna.repository;

import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    //Answer Entity와 메핑된 User 조회
    @Query("SELECT a FROM Answer a " +
            "JOIN FETCH a.user " +
            "WHERE a.answerId = :answerId ")
    Optional<Answer> findByIdWithUser(Long answerId);

    // 자식 답변(대댓글) 존재 여부 확인
    @Query("SELECT COUNT(a) > 0 FROM Answer a WHERE a.parentAnswer.answerId = :answerId AND a.deletedAt IS NULL")
    boolean existsChildAnswersByParentId(Long answerId);
}
