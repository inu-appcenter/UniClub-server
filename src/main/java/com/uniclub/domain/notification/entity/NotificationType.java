package com.uniclub.domain.notification.entity;

import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;

public enum NotificationType {
    SYSTEM,
    FEDERATION,
    CLUB,
    PERSONAL;

    public static NotificationType from(String input) {
        for (NotificationType notificationType : values()) {
            if (notificationType.name().equals(input)) {
                return notificationType;
            }
        }
        throw new CustomException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
    }
}
