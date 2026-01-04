package com.waaw.common.bean;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class FeignBusinessExceptionAspect {
    @Around("execution(* com.waaw.feign.MyClient.*(..))") // 修改为你的包路径和接口方法名
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed(); // 继续执行原方法调用Feign客户端
        } catch (CustomBusinessException e) { // 捕获特定业务异常并处理或重新抛出
            throw e; // 直接抛出原始异常或包装后的异常
        } catch (Exception e) { // 处理其他异常或记录日志等操作，然后可以选择重新抛出或返回特定值等。
            throw e; // 可以选择抛出或根据需求处理。
        }
    }

}
