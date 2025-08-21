package com.uniclub.domain.qna.repository;

import com.uniclub.domain.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    //Question Entity와 메핑된 User 조회
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.user " +
            "WHERE q.questionId = :questionId " +
            "AND q.isDeleted = false")
    Optional<Question> findByIdWithUserAndNotDeleted(Long questionId);
}
