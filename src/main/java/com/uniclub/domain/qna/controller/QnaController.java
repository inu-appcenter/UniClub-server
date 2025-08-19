package com.uniclub.domain.qna.controller;

import com.uniclub.domain.qna.dto.QustionCreateRequestDto;
import com.uniclub.domain.qna.service.QnaService;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qna")
public class QnaController {
    private final QnaService qnaService;

    /*
    //질문 전체 조회
    @GetMapping
    public ResponseEntity<> getQuestions(@RequestParam Long clubId){

    }

    //특정 질문 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<> getQuestion(@PathVariable Long questionId, @RequestParam Long clubId){

    }

     */

    //질문 등록
    @PostMapping
    public ResponseEntity<Void> createQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long clubId, QustionCreateRequestDto qustionCreateRequestDto) {
        qnaService.createQuestion(userDetails, clubId, qustionCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    //질문 수정
    @PutMapping("/{questionId}")
    public ResponseEntity<> updateQuestion(@PathVariable Long questionId, @RequestParam Long clubId){

    }

    //질문 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<> deleteQuestion(@PathVariable Long questionId, @RequestParam Long clubId){

    }

    //답변 등록
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<> createAnswer(@PathVariable Long questionId, @RequestParam Long clubId){

    }

    //답변 수정
    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<> updateAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @RequestParam Long clubId){

    }

    //답변 삭제
    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<> deleteAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @RequestParam Long clubId){

    }

    */

}
