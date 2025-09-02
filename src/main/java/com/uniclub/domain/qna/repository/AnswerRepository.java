package com.uniclub.domain.qna.repository;

import com.uniclub.domain.qna.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    //Answer Entity와 메핑된 User, Question 조회
    @Query("SELECT a FROM Answer a " +
            "JOIN FETCH a.user " +
            "JOIN FETCH a.question " +
            "WHERE a.answerId = :answerId " +
            "AND a.deleted = false")
    Optional<Answer> findByIdWithUser(Long answerId);

    // 자식 답변(대댓글) 존재 여부 확인
    @Query("SELECT COUNT(a) > 0 FROM Answer a WHERE a.parentAnswer.answerId = :answerId AND a.deleted = false")
    boolean existsChildAnswersByParentId(Long answerId);

    //questionId로 답변과 User를 fetch join하여 호출 (삭제된 답변 제외하되, 자식답변이 있는 삭제된 답변은 포함)
    @Query("SELECT a FROM Answer a JOIN FETCH a.user " +
           "WHERE a.question.questionId = :questionId " +
           "AND (a.deleted = false OR " +
           "(a.deleted = true AND EXISTS (SELECT 1 FROM Answer child WHERE child.parentAnswer.answerId = a.answerId AND child.deleted = false)))")
    List<Answer> findByQuestionIdWithUser(Long questionId);
}
