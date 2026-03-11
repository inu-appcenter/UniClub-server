package com.uniclub.domain.report.dto;

import com.uniclub.domain.report.entity.Report;
import com.uniclub.domain.report.entity.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "신고 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class ReportCreateRequestDto {

    @Schema(description = "신고 대상 타입", example = "QUESTION, ANSWER")
    @NotNull(message = "신고 대상 타입을 입력해주세요.")
    private ReportTargetType targetType;

    @Schema(description = "신고 대상 ID (questionId 또는 answerId)", example = "42")
    @NotNull(message = "신고 대상 ID를 입력해주세요.")
    private Long targetId;

    @Schema(description = "신고 사유 (선택)", example = "광고성 글입니다.")
    private String reason;

    public Report toReportEntity(Long reportId, ReportTargetType targetType, Long targetId, String reason) {
        return Report.builder()
                .reporterId(reportId)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .build();
    }
}
