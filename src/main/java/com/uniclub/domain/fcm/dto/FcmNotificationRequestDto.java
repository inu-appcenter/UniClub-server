package com.uniclub.domain.fcm.dto;

import com.uniclub.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//추후 클라이언트에서 알림 직접 보낼때 사용
@Getter
@NoArgsConstructor
public class FcmNotificationRequestDto {

    @Schema(description = "제목")
    @NotBlank
    private String title;

    @Schema(description = "내용")
    @NotBlank
    private String content;

    @Schema(description = "알림 타입", example = "SYSTEM, FEDERATION, CLUB, PERSONAL, QNA")
    @NotNull
    private NotificationType notificationType;


    //선택사항
    @Schema(description = "Notification에 맞는 Id 값")
    private Long targetId;

    @Schema(description = "전송할 유저 Id", example = "[1, 2, 3]")
    private List<Long> recipientIds;

}