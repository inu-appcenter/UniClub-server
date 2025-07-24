package com.uniclub.domain.club.repository;

import com.uniclub.domain.club.entity.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<MemberShip, Long> {
}
