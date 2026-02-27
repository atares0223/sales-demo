package com.waaw.feign.api;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.waaw.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class FeignBusinessExceptionAspect {
    @Around("execution(* com.waaw.feign.api..*.*FallbackFactory.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Throwable throwable = (Throwable) args[0];
        if (throwable.getCause() instanceof BusinessException) {
            throw throwable.getCause();
        }
        return pjp.proceed();
    }

}
