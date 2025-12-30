package com.uniclub.domain.qna.repository;

import com.uniclub.domain.qna.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            "AND q.deleted = false")
    Optional<Question> findByIdWithUser(Long questionId);

    //Question Entity와 메핑된 User 조회
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.user " +
            "JOIN FETCH q.club " +
            "WHERE q.questionId = :questionId " +
            "AND q.deleted = false")
    Optional<Question> findByIdWithUserAndClub(Long questionId);

    @Query("SELECT q, " +
            "(SELECT COUNT(a) FROM Answer a WHERE a.question.questionId = q.questionId AND a.deleted = false) " +
            "FROM Question q " +
            "LEFT JOIN FETCH q.user u " +
            "LEFT JOIN FETCH q.club c " +
            "LEFT JOIN FETCH u.profileMedia " +
            "WHERE (:keyword IS NULL OR UPPER(q.content) LIKE UPPER(CONCAT('%', :keyword, '%'))) " +
            "AND (:clubId IS NULL OR q.club.clubId = :clubId) " +
            "AND (:answered = false OR q.answered = :answered) " +
            "AND (:userId IS NULL OR q.user.userId = :userId) " +
            "AND q.deleted = false " +
            "ORDER BY q.updateAt DESC")
    Slice<Object[]> searchQuestionsWithAnswerCount(String keyword, Long clubId, boolean answered, Long userId, Pageable pageable);

    //Question Entity와 메핑된 Club 조회
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.club " +
            "WHERE q.questionId = :questionId " +
            "AND q.deleted = false")
    Optional<Question> findByIdWithClub(Long questionId);
}
