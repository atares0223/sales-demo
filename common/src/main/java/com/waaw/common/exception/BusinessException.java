package com.waaw.common.exception;

import com.waaw.common.enums.ResultCodeEnum;

public class BusinessException extends RuntimeException{
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResultCodeEnum.ERROR.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
