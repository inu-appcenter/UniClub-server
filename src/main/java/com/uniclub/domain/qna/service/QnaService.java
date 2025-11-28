package com.uniclub.domain.qna.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.notification.service.NotificationEventProcessor;
import com.uniclub.domain.qna.dto.*;
import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.domain.qna.repository.AnswerRepository;
import com.uniclub.domain.qna.repository.QuestionRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.s3.S3Service;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final S3Service s3Service;
    private final NotificationEventProcessor notificationEventProcessor;
    private final UserRepository userRepository;

    //Qna 페이지 다중 질문 조회
    @Transactional(readOnly = true)
    public PageQuestionResponseDto<SearchQuestionResponseDto> getSearchQuestions(UserDetailsImpl userDetails, String keyword, Long clubId, boolean answered, boolean onlyMyQuestions, int size) {
        // 존재하는 동아리인지 확인
        if (clubId != null && !clubRepository.existsById(clubId)){
            throw new CustomException(ErrorCode.CLUB_NOT_FOUND);
        }
        Long userId = onlyMyQuestions ? userDetails.getUser().getUserId() : null;

        // 질문 리스트와 답변 개수를 함께 조회
        Pageable pageable = PageRequest.of(0, size);
        Slice<Object[]> questionSlice = questionRepository.searchQuestionsWithAnswerCount(keyword, clubId, answered, userId, pageable);

        // N+1 문제를 해결하기 위해 user - profile map 생성
        List<User> users = extractUserNotAnonymousQuestion(questionSlice);
        Map<User, String> profileMap = buildProfileMap(users);

        // 익명은 프로필 이미지가 안보이도록 Dto slice 생성
        Slice<SearchQuestionResponseDto> dtoSlice = createSearchQuestionResponseDtos(userDetails, questionSlice, profileMap, pageable);
        return new PageQuestionResponseDto<>(dtoSlice.getContent(), dtoSlice.hasNext());
    }


    //단일 질문 조회
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(UserDetailsImpl userDetails, Long questionId) {
        //기존 Quesiton Entity 불러오기
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Long questionAuthorId = question.getUser() != null ? question.getUser().getUserId() : null;

        //questionId로 답변과 User를 fetch join하여 조회 (삭제된 답변은 자식답변이 있는 경우에만 조회)
        List<Answer> answerList = answerRepository.findByQuestionIdWithUser(questionId);

        // 익명이 아닌 질문 + 답변들 추출
        List<User> users = extractUserNotAnonymousQuestionAnswer(question, answerList);
        Map<User, String> profileMap = buildProfileMap(users);

        // 유저 - 익명번호 매핑 map 생성
        Map<Long, Integer> anonymousNumberMap = createAnonymousNumberMap(answerList, questionAuthorId, questionId);

        // QuestionResponseDto 생성
        return buildQuestionResponseDto(userDetails, question, answerList, profileMap, anonymousNumberMap,
                questionAuthorId);
    }

    //질문 등록
    public QuestionCreateResponseDto createQuestion(UserDetailsImpl userDetails, Long clubId, QuestionCreateRequestDto questionCreateRequestDto) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        Question question = questionCreateRequestDto.toQuestionEntity(userDetails.getUser(), club);
        questionRepository.save(question);
        log.info("질문 등록 완료: {}", question.getQuestionId());

        notificationEventProcessor.questionRegistered(question.getQuestionId(), clubId);

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
        Question question = questionRepository.findByIdWithUserAndClub(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //상위 댓글 존재하는지 확인
        Answer parentsAnswer = null;
        if (parentsAnswerId != null) {
            parentsAnswer = answerRepository.findById(parentsAnswerId).orElseThrow(
                    () -> new CustomException(ErrorCode.QUESTION_NOT_FOUND)
            );
        }

        // 동아리 회장 답변인지
        Long clubId = question.getClub().getClubId();
        boolean presidentAnswer = membershipRepository.hasRole(userDetails.getUserId(), clubId, Role.PRESIDENT);

        Answer answer = answerCreateRequestDto.toEntity(userDetails, question, parentsAnswer, presidentAnswer);

        //저장
        answerRepository.save(answer);
        log.info("답변 등록 완료: {}", answer.getAnswerId());

        //푸시 알림 전송 및 알림 엔티티 저장
        notificationEventProcessor.answerRegisterd(questionId, answer.getAnswerId(), question.getContent(), question.getUser().getUserId());
        if (parentsAnswer != null) {    //대댓글 알림
            notificationEventProcessor.replyRegistered(questionId, parentsAnswerId, question.getContent());
        }

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
        Question question = questionRepository.findByIdWithClub(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 해당 동아리의 회장인지 확인
        Role userRole = membershipRepository.findByUserIdAndClubId(userDetails.getUserId(), question.getClub().getClubId())
                .map(MemberShip::getRole)
                .orElse(Role.GUEST);

        if (userRole != Role.PRESIDENT) {
            throw new CustomException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 답변 완료로 표시
        question.markAsAnswered();
        log.info("질문 답변완료 처리: questionId={}, 처리자={}", questionId, userDetails.getStudentId());
    }

    @Transactional(readOnly = true)
    public List<QnaClubResponseDto> getSearchClubs(String keyword) {
        //키워드 공란일시
        if (keyword == null || keyword.isBlank()) {
            return clubRepository.searchAllClubsForQna();
        }
        return clubRepository.searchClubsForQna(keyword);
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
                return "작성자";
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

    private List<AnswerResponseDto> createAnswerResponseDtoList(
            List<Answer> answerList,
            Map<Long, Integer> anonymousNumberMap,
            Long questionAuthorId,
            Long userId,
            Map<User, String> profileMap
    ) {
        List<AnswerResponseDto> answerResponseDtoList = new ArrayList<>();
        for (Answer answer : answerList) {
            Integer anonymousNumber = null;
            // 삭제된 유저가 아니고, 익명이고 글 작성자가 아니면 익명번호 부여, 글 작성자라면 anonymousNumber는 null
            if (answer.isAnonymous() && answer.getUser() != null && !answer.getUser().isDeleted()
                    && !answer.getUser().getUserId().equals(questionAuthorId)) {
                anonymousNumber = anonymousNumberMap.get(answer.getUser().getUserId());
            }

            String displayName = createDisplayName(answer, anonymousNumber, questionAuthorId);
            boolean owner = answer.getUser() != null && !answer.getUser().isDeleted()
                    && answer.getUser().getUserId().equals(userId);
            String answererProfile = getProfile(answer.isAnonymous(), answer.getUser(), profileMap);

            answerResponseDtoList.add(AnswerResponseDto.from(answer, displayName, owner, answererProfile));
        }
        return answerResponseDtoList;
    }

    //
    private Map<User, String> buildProfileMap(List<User> users) {
        if (users.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> userIds = users.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        // userId - profile map 생성, profile 없는 경우 map에 포함 X
        Map<Long, String> idToProfileMap = userRepository.findProfileLinksByUserIds(userIds).stream()
                .filter(row -> row[1] != null)
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> s3Service.getDownloadPresignedUrl((String) row[1])
                ));

        // 호출부의 간편성을 위해 user - profile map 생성
        return users.stream()
                .filter(user -> idToProfileMap.containsKey(user.getUserId()))
                .collect(Collectors.toMap(
                        user -> user,
                        user -> idToProfileMap.get(user.getUserId()),
                        (existing, replacement) -> existing
                ));
    }

    private boolean shouldFetchProfile(boolean isAnonymous, User user) {
        return !isAnonymous && user != null && !user.isDeleted();
    }

    private String getProfile(boolean isAnonymous, User user, Map<User, String> profileMap) {
        // 쿼리로 조회한 질문/댓글 리스트에 한 유저가 남긴 익명, 비익명 글이 둘 다 있다면 익명 글에도 프로필 이미지 조회가 됨.
        // map에서 프로필 이미지를 꺼낼 때도 질문/댓글의 익명 여부를 한 번 더 확인함으로 익명 글에는 프로필 이미지 조회가 안됨을 보장함.
        return shouldFetchProfile(isAnonymous, user)
                ? profileMap.get(user)
                : null;
    }

    // 익명이 아닌 유저들(프로필 이미지 전송이 필요한) 리스트 생성
    private List<User> extractUserNotAnonymousQuestion(Slice<Object[]> questionSlice) {
        return questionSlice.getContent().stream()
                .map(row -> (Question) row[0])
                .filter(question -> shouldFetchProfile(question.isAnonymous(), question.getUser()))
                .map(Question::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private Slice<SearchQuestionResponseDto> createSearchQuestionResponseDtos(UserDetailsImpl userDetails, Slice<Object[]> questionSlice, Map<User, String> profileMap, Pageable pageable) {
        List<SearchQuestionResponseDto> content = questionSlice.getContent().stream()
                .map(row -> {
                    Question question = (Question) row[0];
                    Long answerCount = (Long) row[1];
                    Long questionAuthorId = question.getUser() != null ? question.getUser().getUserId() : null;
                    boolean owner = userDetails.getUserId().equals(questionAuthorId);
                    String profile = getProfile(question.isAnonymous(), question.getUser(), profileMap);
                    String displayName = question.getDisplayName();
                    return SearchQuestionResponseDto.from(question, displayName, owner, answerCount, profile);
                })
                .collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, questionSlice.hasNext());
    }

    private void extractNotAnonymousAnswer(List<Answer> answerList, List<User> users) {
        answerList.stream()
                .filter(answer -> shouldFetchProfile(answer.isAnonymous(), answer.getUser()))
                .map(Answer::getUser)
                .distinct()
                .forEach(users::add);
    }

    private QuestionResponseDto buildQuestionResponseDto(
            UserDetailsImpl userDetails,
            Question question,
            List<Answer> answerList,
            Map<User, String> profileMap,
            Map<Long, Integer> anonymousNumberMap,
            Long questionAuthorId
    ) {
        Long userId = userDetails.getUserId();

        // 질문자 본인 여부
        boolean questionOwner = userId.equals(questionAuthorId);
        String questionerProfile = getProfile(question.isAnonymous(), question.getUser(), profileMap);

        // 답변 리스트
        List<AnswerResponseDto> answerResponseDtoList = createAnswerResponseDtoList(
                answerList, anonymousNumberMap, questionAuthorId, userId, profileMap
        );

        // 동아리 회장 여부
        boolean president = checkIsPresident(userId, question.getClub().getClubId());
        String displayName = question.getDisplayName();

        return QuestionResponseDto.from(question, displayName, answerResponseDtoList, questionOwner,
                questionerProfile, president);
    }

    // 동아리 회장인지 확인
    private boolean checkIsPresident(Long userId, Long clubId) {
        return membershipRepository.findByUserIdAndClubId(userId, clubId)
                .map(membership -> membership.getRole() == Role.PRESIDENT)
                .orElse(false);
    }


    private List<User> extractUserNotAnonymousQuestionAnswer(Question question, List<Answer> answerList) {
        List<User> users = new ArrayList<>();
        // 질문
        if (shouldFetchProfile(question.isAnonymous(), question.getUser())) {
            users.add(question.getUser());
        }
        // 답변
        extractNotAnonymousAnswer(answerList, users);
        return users;
    }


}
