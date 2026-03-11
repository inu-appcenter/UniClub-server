package com.uniclub.domain.report.service;

import com.uniclub.domain.qna.repository.AnswerRepository;
import com.uniclub.domain.qna.repository.QuestionRepository;
import com.uniclub.domain.report.dto.ReportCreateRequestDto;
import com.uniclub.domain.report.entity.Report;
import com.uniclub.domain.report.entity.ReportTargetType;
import com.uniclub.domain.report.repository.ReportRepository;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public void createReport(Long userId, ReportCreateRequestDto requestDto) {
        ReportTargetType targetType = requestDto.getTargetType();
        Long targetId = requestDto.getTargetId();

        validateTargetExists(targetType, targetId);

        Report report = requestDto.toReportEntity(userId, targetType, targetId, requestDto.getReason());

        reportRepository.save(report);
        log.info("신고 접수 완료: reportId={}, reporterId={}, targetType={}, targetId={}", report.getReportId(), userId, targetType, targetId);
    }

    private void validateTargetExists(ReportTargetType targetType, Long targetId) {
        switch (targetType) {
            case QUESTION -> {
                if (!questionRepository.existsById(targetId)) {
                    throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
                }
            }
            case ANSWER -> {
                if (!answerRepository.existsById(targetId)) {
                    throw new CustomException(ErrorCode.ANSWER_NOT_FOUND);
                }
            }
        }
    }
}
