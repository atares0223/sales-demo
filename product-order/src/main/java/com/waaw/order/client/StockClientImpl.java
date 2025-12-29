package com.waaw.order.client;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.stock.GoodDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockClientImpl implements StockClient{
    @Override
    public ApiResponse deductGoods(List<GoodDTO> goodDTO) {
        return ApiResponse.error("something wrong with stock service");
    }
}
