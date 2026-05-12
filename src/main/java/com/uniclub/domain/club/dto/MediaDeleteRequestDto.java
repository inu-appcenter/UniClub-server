package com.uniclub.domain.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "미디어 삭제 요청 DTO")
@Getter
@NoArgsConstructor
public class MediaDeleteRequestDto {

    @Schema(description = "삭제할 미디어 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "삭제할 미디어 ID 목록을 입력해주세요.")
    private List<Long> mediaIds;
}