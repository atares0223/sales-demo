package com.waaw.feign.api.stock;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.waaw.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StockClientFallbackFactory implements FallbackFactory<StockClient> {
    @Override
    public StockClient create(Throwable cause) {
        return goodDTO -> {
            log.error("StockClient deductGoods fallback: {}", cause.getMessage());
            throw new BusinessException(cause.getMessage());
        };
    }
}
