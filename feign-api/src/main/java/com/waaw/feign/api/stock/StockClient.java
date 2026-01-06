package com.waaw.feign.api.stock;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.waaw.common.ApiResponse;
import com.waaw.common.Constants;
import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.common.exception.BusinessException;

@FeignClient(name = Constants.STOCK_SERVICE
        , fallbackFactory = StockClientFallbackFactory.class
)
public interface StockClient {
    @PostMapping("/goods/deduct")
    ApiResponse<String> deductGoods(@RequestBody List<GoodDTO> goodDTO) throws BusinessException;
}
