package com.uniclub.domain.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "관심동아리 토글 응답 DTO")
@Getter
@AllArgsConstructor
public class ToggleFavoriteResponseDto {
    @Schema(description = "처리 결과 메시지", example = "관심 동아리 등록 완료")
    private final String message;

}
