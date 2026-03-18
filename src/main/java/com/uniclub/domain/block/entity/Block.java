package com.uniclub.domain.block.entity;

import com.uniclub.global.util.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @Column(name = "blocker_id", nullable = false)
    private Long blockerId;

    @Column(name = "blocked_id", nullable = false)
    private Long blockedId;

    @Builder
    private Block(Long blockerId, Long blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    public static Block of(Long blockerId, Long blockedId) {
        return Block.builder()
                .blockerId(blockerId)
                .blockedId(blockedId)
                .build();
    }
}
