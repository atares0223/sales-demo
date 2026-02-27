package com.waaw.feign.api.order;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public ApiResponse<ProductOrderDTO> createOrder(CreateOrderDTO createOrderDTO) throws Throwable {
                log.error("OrderClient createOrder fallback: {}", cause.getMessage());
                throw cause;
                // return ApiResponse.error("Order service is currently unavailable. Please try
                // again later.");
            }

            @Override
            public ApiResponse<ProductOrderDTO> getOrder(Long id) {
                log.error("OrderClient getOrder fallback: {}", cause.getMessage());
                return ApiResponse.error("Order service is currently unavailable. Please try again later.");
            }
        };
    }
}
