package com.waaw.customer.client;

import com.waaw.common.ApiResponse;
import com.waaw.common.Constants;
import com.waaw.common.conf.FeignErrorDecoder;
import com.waaw.common.conf.ResultFeignDecoder;
import com.waaw.common.domain.order.CreateOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name= Constants.ORDER_SERVICE
//        ,configuration = {FeignErrorDecoder.class, ResultFeignDecoder.class}
)
public interface OrderClient {
    @PostMapping("/create")
    ApiResponse createOrder(@RequestBody CreateOrderDTO createOrderDTO);
}
