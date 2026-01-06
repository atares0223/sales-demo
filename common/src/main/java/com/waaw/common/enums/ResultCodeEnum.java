package com.waaw.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, ""),
    ERROR(500, "Internal Server Error1"),
    ERROR_PARAMETER(499," Invalid Parameter")
    ;
    private final Integer code;
    private final String message;



}
