package com.waaw.feign.api.order;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public ApiResponse createOrder(CreateOrderDTO createOrderDTO) {
                log.error("OrderClient createOrder fallback: {}", cause.getMessage());
                return ApiResponse.error("Order service is currently unavailable. Please try again later.");
            }
        };
    }
}
