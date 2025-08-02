package com.uniclub.domain.user.repository;

import com.uniclub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.studentId = :studentId")
    Optional<User> findByStudentId(@Param("studentId") String studentId);

    boolean existsByStudentId(String studentId);

}
