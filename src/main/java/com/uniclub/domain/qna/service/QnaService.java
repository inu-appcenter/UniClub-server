package com.uniclub.domain.qna.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.qna.dto.*;
import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.domain.qna.repository.AnswerRepository;
import com.uniclub.domain.qna.repository.QuestionRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final ClubRepository clubRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;


    //단일 질문 조회
    public QuestionResponseDto getQuestion(UserDetailsImpl userDetails, Long questionId) {
        //기존 Quesiton Entity 불러오기
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //질문 작성자 여부
        boolean isOwner = question.getUser().getUserId().equals(userDetails.getUser().getUserId());

        return QuestionResponseDto.from(isOwner, question);
    }

    //질문 등록
    public QuestionCreateResponseDto createQuestion(UserDetailsImpl userDetails, Long clubId, QuestionCreateRequestDto questionCreateRequestDto) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        Question question = questionCreateRequestDto.toQuestionEntity(userDetails.getUser(), club);
        questionRepository.save(question);
        log.info("질문 등록 완료: {}", question.getQuestionId());
        return QuestionCreateResponseDto.from(question);
    }


    //질문 수정
    public void updateQuestion(UserDetailsImpl userDetails, Long questionId, QuestionUpdateRequestDto questionUpdateRequestDto) {
        //기존 Quesiton Entity 불러오기 (삭제된 질문 제외)
        Question existingQuestion = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //Question 소유주 검증(Fetch Join으로 호출하여 추가 쿼리 X)
        if (!existingQuestion.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //답변이 이미 채택된 질문은 수정 불가
        if (existingQuestion.isAnswered()) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_ANSWERED_QUESTION);
        }

        //업데이트
        existingQuestion.update(
                questionUpdateRequestDto.getContent(),
                questionUpdateRequestDto.getIsAnnonymous(),
                questionUpdateRequestDto.getIsAnswered()
        );

        log.info("질문 수정 완료: {}", existingQuestion.getQuestionId());
    }


    //질문 삭제
    public void deleteQuestion(UserDetailsImpl userDetails, Long questionId) {
        //기존 Quesiton Entity 불러오기
        Question existingQuestion = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //Question 소유주 검증(Fetch Join으로 호출하여 추가 쿼리 X)
        if (!existingQuestion.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        questionRepository.delete(existingQuestion);

        log.info("질문 삭제 완료: {}", existingQuestion.getQuestionId());
    }


    //답변 등록
    public AnswerCreateResponseDto createAnswer(UserDetailsImpl userDetails, Long questionId, Long parentsAnswerId, AnswerCreateRequestDto answerCreateRequestDto) {
        //존재하는 질문인지 확인
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //상위 댓글 존재하는지 확인
        Answer parentsAnswer = answerRepository.findById(parentsAnswerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        Answer answer = answerCreateRequestDto.toEntity(userDetails, question, parentsAnswer);

        answerRepository.save(answer);

        log.info("답변 등록 완료: {}", answer.getAnswerId());
        return AnswerCreateResponseDto.from(answer);
    }


    //답변 삭제
    public void deleteAnswer(UserDetailsImpl userDetails, Long answerId) {
        //기존 Answer Entity 불러오기
        Answer existingAnswer = answerRepository.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        //Answer 소유주 검증(Fetch Join으로 호출하여 추가 쿼리 X)
        if (!existingAnswer.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //매핑되어 있는 자식 Answer Entity(대댓글)이 존재한다면
        if (answerRepository.existsChildAnswersByParentId(answerId)) {
            existingAnswer.softDelete();
        } else {    //대댓글이 존재하지 않는다면
            answerRepository.delete(existingAnswer);
        }
    }
}
