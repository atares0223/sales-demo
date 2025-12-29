package com.waaw.customer.controller;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.customer.client.OrderClient;
import com.waaw.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final OrderClient orderClient;

    @GetMapping("/{id}")
    public ApiResponse findCustomerById(@PathVariable("id") Long id) throws InterruptedException {
        log.info("findCustomerById {}",id);
        if(Objects.equals(id,2L)){
            Thread.sleep(40);
        }
        return ApiResponse.success(customerService.findById(id));
    }

    @PostMapping("/create/order")
    public ApiResponse createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO){
        return orderClient.createOrder(createOrderDTO);
    }

}
