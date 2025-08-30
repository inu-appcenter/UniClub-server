package com.uniclub.domain.qna.controller;

import com.uniclub.domain.qna.dto.*;
import com.uniclub.domain.qna.service.QnaService;
import com.uniclub.global.security.UserDetailsImpl;
import com.uniclub.global.swagger.QnaApiSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qna")
public class QnaController implements QnaApiSpecification {
    private final QnaService qnaService;


    //QnA 페이지 search
    @GetMapping("/search")
    public ResponseEntity<PageQuestionResponseDto<SearchQuestionResponseDto>> getSearchQuestions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long clubId,
            @RequestParam(defaultValue = "false") boolean answered,
            @RequestParam(defaultValue = "false") boolean onlyMyQuestions,
            @RequestParam(defaultValue = "10") int size
    ) {
        Slice<SearchQuestionResponseDto> slice = qnaService.getSearchQuestions(userDetails, keyword, clubId, answered, onlyMyQuestions, size);
        PageQuestionResponseDto<SearchQuestionResponseDto> searchQuestionResponseDtoList = new PageQuestionResponseDto<>(slice.getContent(), slice.hasNext());
        return ResponseEntity.status(HttpStatus.OK).body(searchQuestionResponseDtoList);
    }


    //특정 질문 조회 (답변 리스트 추가전)
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponseDto> getQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId){
        QuestionResponseDto questionResponseDto = qnaService.getQuestion(userDetails, questionId);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }


    //질문 등록
    @PostMapping
    public ResponseEntity<QuestionCreateResponseDto> createQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long clubId, @Valid @RequestBody QuestionCreateRequestDto questionCreateRequestDto) {
        QuestionCreateResponseDto questionCreateResponseDto = qnaService.createQuestion(userDetails, clubId, questionCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionCreateResponseDto);
    }


    //질문 수정
    @PatchMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId, @Valid @RequestBody QuestionUpdateRequestDto questionUpdateRequestDto){
        qnaService.updateQuestion(userDetails, questionId, questionUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //질문 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long questionId){
        qnaService.deleteQuestion(userDetails, questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    //답변 등록 및 대댓글
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerCreateResponseDto> createAnswer(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId, @RequestParam Long parentsAnswerId, @Valid @RequestBody AnswerCreateRequestDto answerCreateRequestDto){
        AnswerCreateResponseDto answerCreateResponseDto = qnaService.createAnswer(userDetails, questionId, parentsAnswerId, answerCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(answerCreateResponseDto);
    }


    //답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long answerId){
        qnaService.deleteAnswer(userDetails, answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //회장이 질문을 답변 완료로 표시
    @PatchMapping("/{questionId}/answered")
    public ResponseEntity<Void> markQuestionAsAnswered(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long questionId){
        qnaService.markQuestionAsAnswered(userDetails, questionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /*
    //신고
    @PostMapping("/reports")
    public ResponseEntity<> reportsQna(){

    }

     */
}
