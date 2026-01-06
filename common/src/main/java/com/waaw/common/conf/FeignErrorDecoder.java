package com.waaw.common.conf;

import com.waaw.common.ExceptionInfo;
import com.waaw.common.enums.ResultCodeEnum;
import com.waaw.common.exception.BusinessException;
import com.waaw.common.gson.GsonUtil;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author kent
 */
@Slf4j
@Configuration
public class FeignErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = super.decode(methodKey, response);

        // 如果是RetryableException，则返回继续重试
        if (exception instanceof RetryableException) {
            return exception;
        }

        try {
            // 如果是FeignException，则对其进行处理，并抛出BusinessException
            if (exception instanceof FeignException feignException && feignException.responseBody().isPresent()) {
                ByteBuffer responseBody = feignException.responseBody().get();
                String bodyText = StandardCharsets.UTF_8.newDecoder().decode(responseBody.asReadOnlyBuffer()).toString();
                // 将异常信息，转换为ExceptionInfo对象
                ExceptionInfo exceptionInfo = GsonUtil.gson.fromJson(bodyText, ExceptionInfo.class);
                // 如果excepiton中code不为空，则使用该code，否则使用默认的错误code
                Integer code = Optional.ofNullable(exceptionInfo.getCode()).orElse(ResultCodeEnum.ERROR.getCode());
                // 如果excepiton中message不为空，则使用该message，否则使用默认的错误message
                String message = Optional.ofNullable(exceptionInfo.getMessage()).orElse(ResultCodeEnum.ERROR.getMessage());
                return new BusinessException(code, message);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return exception;
    }
}
