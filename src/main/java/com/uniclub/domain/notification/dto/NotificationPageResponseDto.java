package com.uniclub.domain.notification.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "알림 조회 응답 DTO(Page)")
@Getter
public class NotificationPageResponseDto {

    @Schema(description = "알림 목록")
    private final List<NotificationResponseDto> notifications;

    @Schema(description = "현재 페이지", example = "1")
    private final int currentPage;

    @Schema(description = "총 페이지", example = "1")
    private final int totalPages;

    @Schema(description = "총 갯수", example = "1")
    private final long totalElements;

    @Schema(description = "다음 페이지 여부", example = "1")
    private final boolean hasNext;

    @Builder
    public NotificationPageResponseDto(List<NotificationResponseDto> notifications, int currentPage, int totalPages, long totalElements, boolean hasNext) {
        this.notifications = notifications;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }

    public static NotificationPageResponseDto from(Page<NotificationResponseDto> page) {
        return NotificationPageResponseDto.builder()
                .notifications(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}
