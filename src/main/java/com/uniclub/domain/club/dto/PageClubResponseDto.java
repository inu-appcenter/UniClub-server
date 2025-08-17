package com.uniclub.domain.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "페이징 응답 DTO")
@AllArgsConstructor
@Getter
public class PageClubResponseDto<T> {
    @Schema(description = "조회된 동아리 목록")
    private final List<T> content;
    
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private final boolean hasNext;
}
