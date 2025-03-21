package com.BINexus.back.exception;


import com.BINexus.back.common.ErrorCode;

/**
 * 抛异常工具类
 *
 * 
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, BusinessException businessException) {
        if (condition) {
            throw businessException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }


    public static void throwIf(boolean condition, String message) {
        throwIf(condition, new BusinessException(message));
    }
}
