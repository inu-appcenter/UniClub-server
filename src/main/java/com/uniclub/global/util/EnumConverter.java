package com.uniclub.global.util;

import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;

public class EnumConverter {
    
    public static <T extends Enum<T>> T stringToEnum(String value, Class<T> enumClass, ErrorCode errorCode) {
        T[] enumConstants = enumClass.getEnumConstants();
        
        for (T enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                return enumConstant;
            }
        }
        
        throw new CustomException(errorCode);
    }
}