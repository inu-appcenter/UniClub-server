package com.uniclub.domain.category.entity;

import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;

public enum CategoryType {
    LIBERAL_ACADEMIC,   //교양학술
    HOBBY_EXHIBITION,   //취미전시
    SPORTS, // 체육
    RELIGION,   //종교
    VOLUNTEER,  //봉사
    CULTURE;    //문화

    // 카테고리 enum 타입, 문자열 비교 및 예외처리
    public static CategoryType from(String input) {
        for (CategoryType category : values()) {
            if (category.name().equals(input)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
    }

}
