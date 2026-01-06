package com.waaw.common.bean;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.waaw.common.ApiResponse;
import com.waaw.common.exception.BusinessException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private void logError(String url, String message) {
        log.error("URL:{} ,{}", url, message);
    }

    @ExceptionHandler({BindException.class})
    public ApiResponse<Object> exceptionHandler(BindException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            String field = fieldError.getField();
            String msg = fieldError.getDefaultMessage();
            String errorMsg = field + " : " + msg;
            logError(request.getRequestURI(),errorMsg);
            return ApiResponse.error(errorMsg);
        }
        return ApiResponse.error();
    }

    @ExceptionHandler(FeignException.class)
    public ApiResponse<Object> exceptionHandler(FeignException e, HttpServletRequest request) {
        String errorMsg = e.getMessage();
        logError(request.getRequestURI(),errorMsg);
        return ApiResponse.error(errorMsg);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> exceptionHandler(BusinessException e, HttpServletRequest request) {
        String errorMsg = e.getMessage();
        logError(request.getRequestURI(),errorMsg);
        return ApiResponse.error(errorMsg);
    }

}