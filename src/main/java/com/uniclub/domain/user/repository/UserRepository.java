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

    @Query("SELECT u FROM User u WHERE u.deleted = true AND u.deletedAt < :cutoffDate")
    List<User> findByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);

    @Query("SELECT u.profile FROM User u WHERE u.userId = :userId")
    Optional<String> findProfileLinkByUserId(@Param("userId") Long userId);

    @Query("SELECT u.userId, u.profile FROM User u WHERE u.userId IN :userIds AND u.deleted = false")
    List<Object[]> findProfileLinksByUserIds(@Param("userIds") List<Long> userIds);
}
