package com.uniclub.domain.qna.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final ClubRepository clubRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MembershipRepository membershipRepository;


    //Qna 페이지 다중 질문 조회
    public Slice<SearchQuestionResponseDto> getSearchQuestions(UserDetailsImpl userDetails, String keyword, Long clubId, boolean answered, boolean onlyMyQuestions, int size) {
        // 존재하는 동아리인지 확인
        if (!clubRepository.existsById(clubId)){
            throw new CustomException(ErrorCode.CLUB_NOT_FOUND);
        }

        //clubId 확인
        Long userId = onlyMyQuestions ? userDetails.getUser().getUserId() : null;

        //PageRequest 생성 및 정렬
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Slice<Object[]> resultSlice = questionRepository.searchQuestionsWithAnswerCount(keyword, clubId, answered, userId, pageable);

        // DTO 변환
        List<SearchQuestionResponseDto> content = resultSlice.getContent().stream()
                .map(row -> {
                    Question question = (Question) row[0];
                    Long answerCount = (Long) row[1];   //답변 갯수
                    return SearchQuestionResponseDto.from(question, answerCount);
                })
                .collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, resultSlice.hasNext());
    }


    //단일 질문 조회
    public QuestionResponseDto getQuestion(UserDetailsImpl userDetails, Long questionId) {
        //기존 Quesiton Entity 불러오기
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //questionId로 답변과 User를 fetch join하여 조회 (삭제된 답변 제외하되, 자식답변이 있는 삭제된 답변은 포함)
        List<Answer> answerList = answerRepository.findByQuestionIdWithUser(questionId);

        //반환할 Answer
        List<AnswerResponseDto> answerResponseDtoList = new ArrayList<>();

        Long userId = userDetails.getUserId();
        for (Answer answer : answerList) {
            answerResponseDtoList.add(AnswerResponseDto.from(answer, userId));
        }

        // 동아리 회장 여부 확인
        boolean president = membershipRepository.findByUserIdAndClubId(userId, question.getClub().getClubId())
                .map(membership -> membership.getRole() == Role.PRESIDENT)
                .orElse(false);
        
        return QuestionResponseDto.from(question, answerResponseDtoList, userId, president);
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
                questionUpdateRequestDto.getAnonymous(),
                questionUpdateRequestDto.getAnswered()
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

        existingQuestion.softDelete();

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

        //저장
        answerRepository.save(answer);

        log.info("답변 등록 완료: {}", answer.getAnswerId());
        return AnswerCreateResponseDto.from(answer);
    }


    //답변 삭제
    public void deleteAnswer(UserDetailsImpl userDetails, Long answerId) {
        //기존 Answer Entity 불러오기 (Question도 함께 fetch join)
        Answer existingAnswer = answerRepository.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        //Answer 소유주 검증(Fetch Join으로 호출하여 추가 쿼리 X)
        if (!existingAnswer.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        //답변 완료된 질문의 답변은 삭제 불가
        if (existingAnswer.getQuestion().isAnswered()) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_ANSWER_ANSWERED_QUESTION);
        }

        existingAnswer.softDelete();
        
        log.info("답변 삭제 완료: {}", existingAnswer.getAnswerId());
    }

    //회장이 질문을 답변 완료로 표시
    public void markQuestionAsAnswered(UserDetailsImpl userDetails, Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 해당 동아리의 회장인지 확인
        Role userRole = membershipRepository.findByUserIdAndClubId(userDetails.getUserId(), question.getClub().getClubId())
                .map(membership -> membership.getRole())
                .orElse(Role.GUEST);

        if (userRole != Role.PRESIDENT) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 답변 완료로 표시
        question.markAsAnswered();
        log.info("질문 답변완료 처리: questionId={}, 처리자={}", questionId, userDetails.getStudentId());
    }

}
