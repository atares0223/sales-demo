package com.waaw.common.bean;

import com.waaw.common.ApiResponse;
import com.waaw.common.Constants;
import com.waaw.common.exception.BusinessException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    @ExceptionHandler({BindException.class})
    public ApiResponse exceptionHandler(BindException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String field = fieldError.getField();
        String msg = fieldError.getDefaultMessage();
        String errorMsg = field+" : "+msg;
        log.error("URL:{} ,{}", request.getRequestURI(),errorMsg);
        return ApiResponse.error(errorMsg);
    }

    @ExceptionHandler(FeignException.class)
    public ApiResponse exceptionHandler(FeignException e, HttpServletRequest request) {
        String errorMsg = e.getMessage();
        log.error("URL:{} ,{}", request.getRequestURI(),errorMsg);
        return ApiResponse.error(errorMsg);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse exceptionHandler(BusinessException e, HttpServletRequest request) {
        String errorMsg = e.getMessage();
        log.error("URL:{} ,{}", request.getRequestURI(),errorMsg);
        return ApiResponse.error(errorMsg);
    }

//    @ExceptionHandler({BindException.class})
//    @ResponseBody
//    public ApiResponse bindExceptionHandler(BindException exception) {
//        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
//        String errorMessage = fieldErrors.get(0).getDefaultMessage();
//        log.error(errorMessage, exception);
//        return ApiResponse.error(ResultCodeEnum.ERROR_PARAMETER.getCode(), errorMessage);
//    }

//    @ExceptionHandler({BusinessException.class})
//    public ApiResponse businessExceptionHandler(BusinessException exception) {
//        log.error(exception.getMessage(), exception);
//        return ApiResponse.error(exception.getCode(), exception.getMessage());
//    }

}