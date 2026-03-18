package com.uniclub.domain.block.repository;

import com.uniclub.domain.block.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    @Query("SELECT b.blockedId FROM Block b WHERE b.blockerId = :blockerId")
    List<Long> findBlockedIdsByBlockerId(Long blockerId);

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
