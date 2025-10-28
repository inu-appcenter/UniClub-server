package com.uniclub.domain.notification.service;

import com.uniclub.domain.club.entity.Club;
import com.uniclub.domain.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationEventProcessor notificationEventProcessor;
    private final ClubRepository clubRepository;

    //모집 마감 임박 동아리 확인 (7, 3, 1일 전)
    @Scheduled(cron = "0 0 9 * * *")
    public void checkClubRecruitmentNotifications() {
        log.info("동아리 모집 관련 알림 스케줄링 실행");

        try {
            // 1. 오늘 모집 시작된 동아리들 확인
            checkRecruitmentStarted();

            // 2. 모집 마감 임박 동아리들 확인 (7, 3, 1일 전)
            checkRecruitmentDeadline();

        } catch (Exception e) {
            log.error("동아리 모집 알림 스케줄링 중 오류 발생", e);
        }
    }

    private void checkRecruitmentStarted() {
        log.info("동아리 모집 시작 알림 스케줄링 실행");

        try {
            //24시간 이내 모집이 시작된 동아리 조회
            List<Club> startedClubs = clubRepository.findRecruitmentStartedInLast24Hours();

            for (Club club : startedClubs) {
                notificationEventProcessor.clubRecruitmentStarted(club.getClubId());
            }

            log.info("모집 시작 동아리 처리 완료: {}개", startedClubs.size());

        } catch (Exception e) {
            log.warn("모집 시작 동아리 확인 중 오류", e);
        }
    }


    private void checkRecruitmentDeadline() {
        log.info("동아리 모집 마감 임박 알림 스케줄링 실행");

        try {
            // 7일 후 마감
            List<Club> clubs7Days = clubRepository.findRecruitmentEndingInDays(7);
            for (Club club : clubs7Days) {
                notificationEventProcessor.clubRecruitmentDeadLine(club.getClubId(), 7);
            }

            // 3일 후 마감
            List<Club> clubs3Days = clubRepository.findRecruitmentEndingInDays(3);
            for (Club club : clubs3Days) {
                notificationEventProcessor.clubRecruitmentDeadLine(club.getClubId(), 3);
            }

            // 1일 후 마감
            List<Club> clubs1Day = clubRepository.findRecruitmentEndingInDays(1);
            for (Club club : clubs1Day) {
                notificationEventProcessor.clubRecruitmentDeadLine(club.getClubId(), 1);
            }

            int totalClubs = clubs7Days.size() + clubs3Days.size() + clubs1Day.size();
            log.info("모집 마감 임박 동아리 처리 완료: {}개 (7일:{}, 3일:{}, 1일:{})",
                    totalClubs, clubs7Days.size(), clubs3Days.size(), clubs1Day.size());

        } catch (Exception e) {
            log.warn("모집 마감 임박 확인 중 오류", e);
        }
    }


}

