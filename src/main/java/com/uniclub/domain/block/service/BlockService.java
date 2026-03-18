package com.uniclub.domain.block.service;

import com.uniclub.domain.block.entity.Block;
import com.uniclub.domain.block.repository.BlockRepository;
import com.uniclub.domain.qna.entity.Answer;
import com.uniclub.domain.qna.entity.Question;
import com.uniclub.domain.qna.repository.AnswerRepository;
import com.uniclub.domain.qna.repository.QuestionRepository;
import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockService {

    private final BlockRepository blockRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public void blockByQuestion(Long blockerId, Long questionId) {
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        block(blockerId, resolveAuthor(question.getUser()).getUserId());
    }

    public void blockByAnswer(Long blockerId, Long answerId) {
        Answer answer = answerRepository.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        block(blockerId, resolveAuthor(answer.getUser()).getUserId());
    }

    private void block(Long blockerId, Long targetId) {
        validateNotSelf(blockerId, targetId);
        validateNotAlreadyBlocked(blockerId, targetId);

        // 양방향 차단: 양쪽 모두 상대방 글이 안 보이도록
        blockRepository.saveAll(List.of(Block.of(blockerId, targetId), Block.of(targetId, blockerId)));
    }

    private User resolveAuthor(User user) {
        if (user == null || user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private void validateNotSelf(Long blockerId, Long targetId) {
        if (blockerId.equals(targetId)) {
            throw new CustomException(ErrorCode.CANNOT_BLOCK_SELF);
        }
    }

    private void validateNotAlreadyBlocked(Long blockerId, Long targetId) {
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, targetId)) {
            throw new CustomException(ErrorCode.ALREADY_BLOCKED);
        }
    }
}
