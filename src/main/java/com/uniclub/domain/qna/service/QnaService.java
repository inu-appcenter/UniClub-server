package com.uniclub.domain.qna.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.entity.MemberShip;
import com.uniclub.domain.club.entity.Role;
import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.domain.club.repository.MembershipRepository;
import com.uniclub.domain.notification.service.NotificationEventProcessor;
import com.uniclub.domain.qna.dto.*;
import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.domain.qna.entity.QuestionAnonymousUser;
import com.uniclub.domain.qna.repository.AnswerRepository;
import com.uniclub.domain.qna.repository.QuestionAnonymousUserRepository;
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
    private final QuestionAnonymousUserRepository anonymousUserRepository;
    private final MembershipRepository membershipRepository;
    private final S3Service s3Service;
    private final NotificationEventProcessor notificationEventProcessor;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

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

        // 익명이 아닌 유저의 프로필 조회
        List<Long> userIds = extractUserNotAnonymousQuestion(questionSlice);
        Map<Long, String> userProfileMap = buildUserProfileMap(userIds);

        // 동아리 프로필 조회 (회장이 작성한 질문용)
        List<Long> clubIds = extractClubIdsFromQuestions(questionSlice);
        Map<Long, String> clubProfileMap = buildClubProfileMap(clubIds);

        // 익명은 프로필 이미지가 안보이도록 Dto slice 생성
        Slice<SearchQuestionResponseDto> dtoSlice = createSearchQuestionResponseDtos(userDetails, questionSlice, userProfileMap, clubProfileMap, pageable);
        log.info("질문 리스트 조회 완료");
        return new PageQuestionResponseDto<>(dtoSlice.getContent(), dtoSlice.hasNext());
    }


    //단일 질문 조회
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(UserDetailsImpl userDetails, Long questionId) {
        Question question = questionRepository.findByIdWithUserAndClub(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        List<Answer> answerList = answerRepository.findByQuestionIdWithUser(questionId);

        // 익명이 아닌 유저 프로필 조회
        List<Long> userIds = extractUserNotAnonymousQuestionAnswer(question, answerList);
        Map<Long, String> userProfileMap = buildUserProfileMap(userIds);

        // 동아리 프로필 조회 (회장이 작성한 질문/답변용)
        List<Long> clubIds = extractClubIdsFromQuestionsAnswers(question, answerList);
        Map<Long, String> clubProfileMap = buildClubProfileMap(clubIds);

        Map<Long, Integer> anonymousOrderMap = getAnonymousOrderMap(questionId);

        QuestionResponseDto questionResponseDto = buildQuestionResponseDto(userDetails, question, answerList, userProfileMap, clubProfileMap, anonymousOrderMap);
        log.info("질문 조회 완료: questionId = {}", questionId);
        return questionResponseDto;
    }

    //질문 등록
    public QuestionCreateResponseDto createQuestion(UserDetailsImpl userDetails, Long clubId, QuestionCreateRequestDto questionCreateRequestDto) {
        // 존재하는 동아리인지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 회장 여부 확인
        boolean presidentQuestion = membershipRepository.hasRole(userDetails.getUserId(), clubId, Role.PRESIDENT);

        Question question = questionCreateRequestDto.toQuestionEntity(userDetails.getUser(), club, presidentQuestion);
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
                questionUpdateRequestDto.getAnonymous()
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
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Answer parentsAnswer = null;
        if (parentsAnswerId != null) {
            parentsAnswer = answerRepository.findById(parentsAnswerId).orElseThrow(
                    () -> new CustomException(ErrorCode.ANSWER_NOT_FOUND)
            );
        }

        Long clubId = question.getClub().getClubId();
        boolean presidentAnswer = membershipRepository.hasRole(userDetails.getUserId(), clubId, Role.PRESIDENT);

        Answer answer = answerCreateRequestDto.toEntity(userDetails, question, parentsAnswer, presidentAnswer);
        answerRepository.save(answer);
        log.info("답변 등록 완료: {}", answer.getAnswerId());

        // 익명 답변이면 중간 테이블에 매핑 저장
        if (answerCreateRequestDto.isAnonymous()) {
            assignAnonymousOrderIfNeeded(question, userDetails.getUser(), presidentAnswer);
        }


        // 푸시 알림 전송 및 알림 엔티티 저장
        notificationEventProcessor.answerRegisterd(questionId, answer.getAnswerId(), question.getContent(), question.getUser().getUserId());
        if (parentsAnswer != null) {   // 대댓글 알림
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


    // =============================================
    // 익명 번호 관련
    // =============================================

    private Map<Long, Integer> getAnonymousOrderMap(Long questionId) {
        return anonymousUserRepository.findAllByQuestionId(questionId).stream()
                .filter(qau -> qau.getUser() != null && !qau.getUser().isDeleted())
                .collect(Collectors.toMap(
                        qau -> qau.getUser().getUserId(),
                        QuestionAnonymousUser::getAnonymousOrder
                ));
    }

    private void assignAnonymousOrderIfNeeded(Question question, User user, boolean isPresident) {

        Long questionAuthorId = question.getUser() != null ? question.getUser().getUserId() : null;
        boolean isQuestionAuthor = user.getUserId().equals(questionAuthorId);

        // 회장은 익명 번호 부여 안함
        if (isPresident) {
            return;
        }

        // 질문 익명 + 작성자 익명 답변 → 익명 번호 부여 안함 ("작성자"로 표시)
        if (isQuestionAuthor && question.isAnonymous()) {
            return;
        }

        // 질문 실명 + 작성자 익명 답변 → 익명 번호 부여 ("익명{번호}"로 표시)
        Long questionId = question.getQuestionId();
        Long userId = user.getUserId();

        // 이미 익명 번호가 있으면 스킵
        if (anonymousUserRepository.existsByQuestionQuestionIdAndUserUserId(questionId, userId)) {
            return;
        }

        // 새 익명 번호 할당
        Integer maxOrder = anonymousUserRepository.findMaxAnonymousOrderByQuestionId(questionId);
        Integer newOrder = maxOrder + 1;

        QuestionAnonymousUser anonymousUser = QuestionAnonymousUser.create(question, user, newOrder);
        anonymousUserRepository.save(anonymousUser);
    }

    // =============================================
    // 닉네임 판단 관련
    // =============================================

    private String getQuestionNickname(Question question) {
        // 회장이 작성한 질문이면 동아리명 반환
        if (question.isPresidentQuestion()) {
            return question.getClub().getName();
        }
        if (question.isAnonymous()) {
            return "익명";
        }
        if (isDeletedUser(question.getUser())) {
            return "탈퇴한 사용자";
        }
        return question.getUser().getNickname();
    }

    private String getAnswerNickname(Answer answer, Long questionAuthorId, Club club,
                                      Map<Long, Integer> anonymousOrderMap, boolean questionAnonymous) {
        if (answer.isPresidentAnswer()) {
            return club.getName();
        }
        if (isDeletedUser(answer.getUser())) {
            return "탈퇴한 사용자";
        }
        if (answer.isAnonymous()) {
            return getAnonymousAnswerNickname(answer, questionAuthorId, anonymousOrderMap, questionAnonymous);
        }
        return getRealNameAnswerNickname(answer, questionAuthorId, questionAnonymous);
    }

    private String getAnonymousAnswerNickname(Answer answer, Long questionAuthorId,
                                               Map<Long, Integer> anonymousOrderMap, boolean questionAnonymous) {
        // Case 1: 질문 익명 + 작성자 익명 답변 → "작성자"
        if (isQuestionAuthor(answer.getUser(), questionAuthorId) && questionAnonymous) {
            return "작성자";
        }
        // Case 4: 질문 실명 + 작성자 익명 답변 → "익명{번호}" (다른 사람처럼)
        Integer order = anonymousOrderMap.get(answer.getUser().getUserId());
        return "익명" + order;
    }

    private String getRealNameAnswerNickname(Answer answer, Long questionAuthorId, boolean questionAnonymous) {
        String nickname = answer.getUser().getNickname();
        // Case 2: 질문 익명 + 작성자 실명 답변 → "{닉네임}" (작성자 표시 안함)
        if (isQuestionAuthor(answer.getUser(), questionAuthorId) && questionAnonymous) {
            return nickname;
        }
        // Case 3: 질문 실명 + 작성자 실명 답변 → "{닉네임}(작성자)"
        if (isQuestionAuthor(answer.getUser(), questionAuthorId)) {
            return nickname + "(작성자)";
        }
        return nickname;
    }

    // =============================================
    // 상태 체크 헬퍼
    // =============================================

    private boolean isDeletedUser(User user) {
        return user == null || user.isDeleted();
    }

    private boolean isQuestionAuthor(User user, Long questionAuthorId) {
        return user != null && user.getUserId().equals(questionAuthorId);
    }

    private boolean isOwner(User user, Long currentUserId) {
        return user != null && !user.isDeleted() && user.getUserId().equals(currentUserId);
    }

    // 동아리 회장인지 확인
    private boolean checkIsPresident(Long userId, Long clubId) {
        return membershipRepository.findByUserIdAndClubId(userId, clubId)
                .map(membership -> membership.getRole() == Role.PRESIDENT)
                .orElse(false);
    }

    // =============================================
    // 프로필 관련
    // =============================================

    private Map<Long, String> buildUserProfileMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return fetchIdToUserProfileMap(userIds);
    }

    private Map<Long, String> buildClubProfileMap(List<Long> clubIds) {
        if (clubIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return fetchIdToClubProfileMap(clubIds);
    }

    private Map<Long, String> fetchIdToUserProfileMap(List<Long> userIds) {
        return userRepository.findProfileLinksByUserIds(userIds).stream()
                .filter(row -> row[1] != null)
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> s3Service.getDownloadPresignedUrl((String) row[1])
                ));
    }

    private Map<Long, String> fetchIdToClubProfileMap(List<Long> clubIds) {
        return mediaRepository.findClubProfilesByClubIds(clubIds).stream()
                .filter(media -> media.getMediaLink() != null)
                .collect(Collectors.toMap(
                        media -> media.getClub().getClubId(),
                        media -> s3Service.getDownloadPresignedUrl(media.getMediaLink())
                ));
    }


    private boolean shouldFetchProfile(boolean isAnonymous, User user) {
        return !isAnonymous && user != null && !user.isDeleted();
    }

    // 쿼리로 조회한 질문/댓글 리스트에 한 유저가 남긴 익명, 비익명 글이 둘 다 있다면 익명 글에도 프로필 이미지 조회가 됨.
    // map에서 프로필 이미지를 꺼낼 때도 질문/댓글의 익명 여부를 한 번 더 확인함으로 익명 글에는 프로필 이미지 조회가 안됨을 보장함.
    private String getProfile(boolean isAnonymous, User user, Map<Long, String> profileMap) {
        return shouldFetchProfile(isAnonymous, user)
                ? profileMap.get(user.getUserId())
                : null;
    }

    // 질문 프로필: 회장이면 동아리 프로필, 아니면 사용자 프로필
    private String getQuestionProfile(Question question, Map<Long, String> userProfileMap, Map<Long, String> clubProfileMap) {
        if (question.isPresidentQuestion()) {
            return clubProfileMap.get(question.getClub().getClubId());
        }
        return getProfile(question.isAnonymous(), question.getUser(), userProfileMap);
    }

    // 답변 프로필: 회장이면 동아리 프로필, 아니면 사용자 프로필
    private String getAnswerProfile(Answer answer, Map<Long, String> userProfileMap, Map<Long, String> clubProfileMap) {
        if (answer.isPresidentAnswer()) {
            return clubProfileMap.get(answer.getQuestion().getClub().getClubId());
        }
        return getProfile(answer.isAnonymous(), answer.getUser(), userProfileMap);
    }

    // 익명이 아닌 유저들(프로필 이미지 전송이 필요한) ID 리스트 생성
    private List<Long> extractUserNotAnonymousQuestion(Slice<Object[]> questionSlice) {
        return questionSlice.getContent().stream()
                .map(row -> (Question) row[0])
                .filter(question -> shouldFetchProfile(question.isAnonymous(), question.getUser()))
                .map(question -> question.getUser().getUserId())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Long> extractUserNotAnonymousQuestionAnswer(Question question, List<Answer> answerList) {
        List<Long> userIds = new ArrayList<>();
        // 질문
        if (shouldFetchProfile(question.isAnonymous(), question.getUser())) {
            userIds.add(question.getUser().getUserId());
        }
        // 답변
        extractNotAnonymousAnswer(answerList, userIds);
        return userIds;
    }

    private void extractNotAnonymousAnswer(List<Answer> answerList, List<Long> userIds) {
        answerList.stream()
                .filter(answer -> shouldFetchProfile(answer.isAnonymous(), answer.getUser()))
                .map(answer -> answer.getUser().getUserId())
                .distinct()
                .forEach(userIds::add);
    }

    // 회장이 작성한 질문/답변의 동아리 ID 추출
    private List<Long> extractClubIdsFromQuestionsAnswers(Question question, List<Answer> answerList) {
        List<Long> clubIds = new ArrayList<>();
        // 회장이 작성한 질문
        if (question.isPresidentQuestion()) {
            clubIds.add(question.getClub().getClubId());
        }
        // 회장이 작성한 답변
        extractClubIdsFromPresidentAnswers(answerList, clubIds);
        return clubIds.stream().distinct().collect(Collectors.toList());
    }

    private void extractClubIdsFromPresidentAnswers(List<Answer> answerList, List<Long> clubIds) {
        answerList.stream()
                .filter(Answer::isPresidentAnswer)
                .map(answer -> answer.getQuestion().getClub().getClubId())
                .distinct()
                .forEach(clubIds::add);
    }

    // 질문 리스트에서 회장이 작성한 질문의 동아리 ID 추출
    private List<Long> extractClubIdsFromQuestions(Slice<Object[]> questionSlice) {
        return questionSlice.getContent().stream()
                .map(row -> (Question) row[0])
                .filter(Question::isPresidentQuestion)
                .map(question -> question.getClub().getClubId())
                .distinct()
                .collect(Collectors.toList());
    }

    // =============================================
    // DTO 변환 관련
    // =============================================

    private QuestionResponseDto buildQuestionResponseDto(
            UserDetailsImpl userDetails,
            Question question,
            List<Answer> answerList,
            Map<Long, String> userProfileMap,
            Map<Long, String> clubProfileMap,
            Map<Long, Integer> anonymousOrderMap
    ) {
        Long userId = userDetails.getUserId();
        Long questionAuthorId = question.getUser() != null ? question.getUser().getUserId() : null;

        boolean questionOwner = userId.equals(questionAuthorId);
        String questionerProfile = getQuestionProfile(question, userProfileMap, clubProfileMap);
        boolean president = checkIsPresident(userId, question.getClub().getClubId());
        String displayName = getQuestionNickname(question);

        List<AnswerResponseDto> answerResponseDtoList = createAnswerResponseDtoList(
                answerList, anonymousOrderMap, questionAuthorId, userId, userProfileMap, clubProfileMap, question.getClub(), question.isAnonymous());

        return QuestionResponseDto.from(question, displayName, answerResponseDtoList, questionOwner,
                questionerProfile, president);
    }

    private List<AnswerResponseDto> createAnswerResponseDtoList(
            List<Answer> answerList,
            Map<Long, Integer> anonymousOrderMap,
            Long questionAuthorId,
            Long currentUserId,
            Map<Long, String> userProfileMap,
            Map<Long, String> clubProfileMap,
            Club club,
            boolean questionAnonymous
    ) {
        return answerList.stream()
                .map(answer -> toAnswerResponseDto(answer, questionAuthorId, currentUserId,
                        club, anonymousOrderMap, userProfileMap, clubProfileMap, questionAnonymous))
                .collect(Collectors.toList());
    }

    private AnswerResponseDto toAnswerResponseDto(
            Answer answer, Long questionAuthorId, Long currentUserId,
            Club club, Map<Long, Integer> anonymousOrderMap, Map<Long, String> userProfileMap,
            Map<Long, String> clubProfileMap, boolean questionAnonymous) {

        String displayName = getAnswerNickname(answer, questionAuthorId, club, anonymousOrderMap, questionAnonymous);
        boolean owner = isOwner(answer.getUser(), currentUserId);
        String profile = getAnswerProfile(answer, userProfileMap, clubProfileMap);

        return AnswerResponseDto.from(answer, displayName, owner, profile);
    }

    private Slice<SearchQuestionResponseDto> createSearchQuestionResponseDtos(
            UserDetailsImpl userDetails, Slice<Object[]> questionSlice,
            Map<Long, String> userProfileMap, Map<Long, String> clubProfileMap, Pageable pageable) {

        List<SearchQuestionResponseDto> content = questionSlice.getContent().stream()
                .map(row -> toSearchQuestionResponseDto(row, userDetails.getUserId(), userProfileMap, clubProfileMap))
                .collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, questionSlice.hasNext());
    }

    private SearchQuestionResponseDto toSearchQuestionResponseDto(
            Object[] row, Long currentUserId, Map<Long, String> userProfileMap, Map<Long, String> clubProfileMap) {

        Question question = (Question) row[0];
        Long answerCount = (Long) row[1];
        Long questionAuthorId = question.getUser() != null ? question.getUser().getUserId() : null;

        boolean owner = currentUserId.equals(questionAuthorId);
        String profile = getQuestionProfile(question, userProfileMap, clubProfileMap);
        String displayName = getQuestionNickname(question);

        return SearchQuestionResponseDto.from(question, displayName, owner, answerCount, profile);
    }
}
