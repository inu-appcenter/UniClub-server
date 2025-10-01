package com.uniclub.domain.user.repository;

import com.uniclub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.studentId = :studentId")
    Optional<User> findByStudentId(@Param("studentId") String studentId);

    boolean existsByStudentId(String studentId);

    @Query("SELECT u FROM User u WHERE u.name IN :usernames")
    List<User> findByUsernames(@Param("usernames") List<String> usernames);

    @Query("SELECT u FROM User u WHERE u.studentId IN :studentIds")
    List<User> findByStudentIds(@Param("studentIds") List<String> studentIds);

    Optional<User> findByName(String username);

    @Query("SELECT u FROM User u WHERE u.deleted = true AND u.deletedAt < :cutoffDate")
    List<User> findByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);
}
