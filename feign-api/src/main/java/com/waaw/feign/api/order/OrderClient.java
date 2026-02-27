package com.waaw.feign.api.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.waaw.common.ApiResponse;
import com.waaw.common.Constants;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;

@FeignClient(name = Constants.ORDER_SERVICE, fallbackFactory = OrderClientFallbackFactory.class)
@Component
public interface OrderClient {
    @PostMapping("/order/create")
    ApiResponse<ProductOrderDTO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) throws Throwable;

    @GetMapping("/order/{id}")
    public ApiResponse<ProductOrderDTO> getOrder(@PathVariable("id") Long id);
}
