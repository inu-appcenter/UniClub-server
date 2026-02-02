package com.uniclub.domain.club.service;

import com.uniclub.domain.club.entity.Media;
import com.uniclub.domain.club.repository.MediaRepository;
import com.uniclub.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class MediaDeleteScheduler {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteMediaFiles() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
        List<Media> mediaToDelete = mediaRepository.findDeletedMediaBeforeDate(cutoffDate);

        log.info("6개월 지난 soft deleted Media 정리 시작: {}개", mediaToDelete.size());

        if (mediaToDelete.isEmpty()) {
            log.info("정리할 Media가 없습니다.");
            return;
        }

        List<String> s3Keys = mediaToDelete.stream()
                .map(Media::getMediaLink)
                .toList();

        try {
            s3Service.deleteFiles(s3Keys);
            mediaRepository.deleteAll(mediaToDelete);

            log.info("====== Media 정리 완료 ======");
            log.info("물리 삭제된 Media: {}개", mediaToDelete.size());
        } catch (Exception e) {
            log.error("S3 파일 배치 삭제 실패: error={}", e.getMessage());
        }

    }
}
