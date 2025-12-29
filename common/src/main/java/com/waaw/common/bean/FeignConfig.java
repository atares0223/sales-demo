package com.waaw.common.bean;

import com.waaw.common.Constants;
import com.waaw.common.conf.FeignErrorDecoder;
import com.waaw.common.conf.ResultFeignDecoder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header(Constants.REQUEST_SOURCE_TYPE, Constants.FEIGN);
        };
    }
    @Bean
    public ResultFeignDecoder resultFeignDecoder() {
        return new ResultFeignDecoder();
    }
    @Bean
    public FeignErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
