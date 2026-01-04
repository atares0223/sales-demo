package com.waaw.order.client;

import com.waaw.common.ApiResponse;
import com.waaw.common.Constants;
import com.waaw.common.conf.FeignErrorDecoder;
import com.waaw.common.domain.stock.GoodDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = Constants.STOCK_SERVICE
//        , configuration = {FeignErrorDecoder.class}
        ,fallback = StockClientImpl.class
)
public interface StockClient {
    @PostMapping("/deduct")
    ApiResponse deductGoods(@RequestBody List<GoodDTO> goodDTO);
}
