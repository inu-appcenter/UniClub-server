package com.uniclub.domain.qna.controller;

import com.uniclub.domain.qna.dto.QuestionCreateRequestDto;
import com.uniclub.domain.qna.dto.QuestionCreateResponseDto;
import com.uniclub.domain.qna.dto.QuestionResponseDto;
import com.uniclub.domain.qna.dto.QuestionUpdateRequestDto;
import com.uniclub.domain.qna.service.QnaService;
import com.uniclub.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
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
     */

    //특정 질문 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponseDto> getQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId){
        QuestionResponseDto questionResponseDto = qnaService.getQuestion(userDetails, questionId);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }


    //질문 등록
    @PostMapping
    public ResponseEntity<QuestionCreateResponseDto> createQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long clubId, @Valid QuestionCreateRequestDto questionCreateRequestDto) {
        QuestionCreateResponseDto questionCreateResponseDto = qnaService.createQuestion(userDetails, clubId, questionCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionCreateResponseDto);
    }


    //질문 수정
    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId, @Valid QuestionUpdateRequestDto questionUpdateRequestDto){
        qnaService.updateQuestion(userDetails, questionId, questionUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //질문 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long questionId){
        qnaService.deleteQuestion(userDetails, questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
    //답변 등록
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<> createAnswer(@PathVariable Long questionId){

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
