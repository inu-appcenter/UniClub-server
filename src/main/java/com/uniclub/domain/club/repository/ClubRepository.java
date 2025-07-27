package com.uniclub.domain.club.repository;

import com.uniclub.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Boolean existsByName(String name);
}
