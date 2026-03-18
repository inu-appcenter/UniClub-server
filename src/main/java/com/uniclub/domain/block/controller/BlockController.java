package com.uniclub.domain.block.controller;

import com.uniclub.domain.block.service.BlockService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.BlockApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/block")
public class BlockController implements BlockApiSpecification {

    private final BlockService blockService;

    @PostMapping("/questions/{questionId}")
    public ResponseEntity<Void> blockByQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    ) {
        blockService.blockByQuestion(userDetails.getUserId(), questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/answers/{answerId}")
    public ResponseEntity<Void> blockByAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long answerId
    ) {
        blockService.blockByAnswer(userDetails.getUserId(), answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
