package com.waaw.common.conf;

import com.waaw.common.ApiResponse;
import com.waaw.common.enums.ResultCodeEnum;
import com.waaw.common.exception.BusinessException;
import com.waaw.common.gson.GsonUtil;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
@RequiredArgsConstructor
public class ResultFeignDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException {
        log.info("调用自定义Feign解码器");

        // 读取响应中数据，将响应体中数据转为字符串
        String bodyString = Util.toString(response.body().asReader(Util.UTF_8));
        ApiResponse<?> apiResponse = GsonUtil.gson.fromJson(bodyString, ApiResponse.class);

        // 如果响应状态为失败，抛出业务异常
        if (!ResultCodeEnum.SUCCESS.getCode().equals(apiResponse.getStatus())) {
            throw new BusinessException(apiResponse.getStatus(), apiResponse.getMessage());
        }
        // 从 Result 中提取 data 字段
        return apiResponse;
    }

}
