package com.BINexus.back.exception;

import com.BINexus.back.common.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 *
 * 
 */


@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;



    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(String message) {
        super(message);
        this.code=-1;
    }

    public int getCode() {
        return code;
    }
}
