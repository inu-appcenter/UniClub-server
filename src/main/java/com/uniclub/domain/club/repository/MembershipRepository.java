package com.uniclub.domain.club.repository;

import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MemberShip, Long> {
    @Query("SELECT m FROM MemberShip m WHERE m.user.userId = :userId AND m.club.clubId = :clubId")
    Optional<MemberShip> findByUserIdAndClubId(Long userId, Long clubId);
}
