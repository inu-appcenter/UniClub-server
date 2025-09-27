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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        //questionId로 답변과 User를 fetch join하여 조회 (삭제된 답변은 자식답변이 있는 경우에만 조회)
        List<Answer> answerList = answerRepository.findByQuestionIdWithUser(questionId);

        Long questionAuthorId = question.getUser().getUserId();
        Long userId = userDetails.getUserId();


        // 유저 - 익명번호 매핑 map 생성
        Map<Long, Integer> anonymousNumberMap = createAnonymousNumberMap(answerList, questionAuthorId, questionId);

        List<AnswerResponseDto> answerResponseDtoList = new ArrayList<>();
        for (Answer answer : answerList) {
            Integer anonymousNumber = null;
            // 삭제된 유저가 아니고, 익명이고 글 작성자가 아니면 익명번호 부여, 글 작성자라면 anonymousNumber는 null
            if (answer.isAnonymous() && answer.getUser() != null && !answer.getUser().isDeleted() && !answer.getUser().getUserId().equals(questionAuthorId)) {
                anonymousNumber = anonymousNumberMap.get(answer.getUser().getUserId());
            }

            // 익명(작성자), 닉네임, 익명1 등 보여지는 이름
            String displayName = createDisplayName(answer, anonymousNumber, questionAuthorId);
            // 댓글 작성자 본인여부
            boolean owner = answer.getUser() != null && !answer.getUser().isDeleted() &&
                    answer.getUser().getUserId().equals(userId);

            answerResponseDtoList.add(AnswerResponseDto.from(answer, displayName, owner));
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


    private Map<Long, Integer> createAnonymousNumberMap(List<Answer> answerList, Long questionAuthorId, Long questionId) {
        Map<Long, Integer> anonymousNumberMap = new HashMap<>();

        // 모든 답변(삭제된 답변 포함)의 최대 익명번호 조회
        Integer maxExistingOrder = answerRepository.findMaxAnonymousOrderByQuestionId(questionId);
        if (maxExistingOrder == null) {
            maxExistingOrder = 0;
        }
        // 새로 생긴 댓글 익명번호는 기존 최대 익명번호 + 1
        int anonymousCounter = maxExistingOrder + 1;

        for (Answer answer : answerList) {
            // 삭제된 답변은 map에 추가 X
            if (answer.isAnonymous() && answer.getUser() != null && !answer.getUser().isDeleted()) {
                Long answerUserId = answer.getUser().getUserId();
                // 질문 작성자가 아닌 경우에만 익명번호 부여, 같은 유저는 같은 익명번호 사용
                if (!answerUserId.equals(questionAuthorId) && !anonymousNumberMap.containsKey(answerUserId)) {
                    // 기존에 익명번호가 부여돼있는 답변은 map에 그대로 들어가고, 새로운 답변은 익명번호 추가
                    if (answer.getAnonymousOrder() == null) {
                        answer.assignAnonymousOrderIfNull(anonymousCounter++);
                    }
                    anonymousNumberMap.put(answerUserId, answer.getAnonymousOrder());
                }
            }
        }
        return anonymousNumberMap;
    }

    private String createDisplayName(Answer answer, Integer anonymousNumber, Long questionAuthorId) {
        boolean isQuestionAuthor = answer.getUser() != null && answer.getUser().getUserId().equals(questionAuthorId);

        // 탈퇴한 사용자인 경우
        if (answer.getUser() == null || answer.getUser().isDeleted()) {
            return "탈퇴한 사용자";
        }

        // 익명인 경우
        else if (answer.isAnonymous()) {
            if (isQuestionAuthor) {
                return "익명(작성자)";
            } else {
                return "익명" + anonymousNumber;
            }
        }

        // 익명이 아닌 경우(닉네임)
        else {
            String displayName = answer.getUser().getNickname();
            if (isQuestionAuthor) {
                displayName += "(작성자)";
            }
            return displayName;
        }
    }

}
