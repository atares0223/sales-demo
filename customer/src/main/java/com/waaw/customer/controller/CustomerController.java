package com.waaw.customer.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;
import com.waaw.customer.domain.Customer;
import com.waaw.customer.service.CustomerService;
import com.waaw.feign.api.order.OrderClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
//@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final OrderClient orderClient;

    @GetMapping("/{id}")
    public ApiResponse<Customer> findCustomerById(@PathVariable("id") Long id) throws InterruptedException {
        log.info("findCustomerById {}",id);
        if(Objects.equals(id,2L)){
            Thread.sleep(40);
        }
        orderClient.getOrder(id);
        return ApiResponse.success(customerService.findById(id));
    }

    @PostMapping("/create/order")
    public ApiResponse<ProductOrderDTO> createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        return orderClient.createOrder(createOrderDTO);
    }

    @GetMapping("/setCookie")
    public ApiResponse<String> setCookie(HttpServletRequest request, HttpServletResponse response, @RequestParam("key") String key , @RequestParam("value") String value) throws InterruptedException {
        log.info("setCookie {}={}",key,value);
        Cookie cookie = new Cookie(key,value);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ApiResponse.success("set cookie success");
    }
    @GetMapping("/getCookie")
    public ApiResponse<String> getCookie(HttpServletRequest request, HttpServletResponse response, @RequestParam("key") String key,@RequestParam(value = "redirecturi",defaultValue = "")String redirecturi) throws InterruptedException, IOException {
        log.info("getCookie {}",key);
        Cookie[] cookies = request.getCookies();
        AtomicReference<String> value = new AtomicReference<>("Not found");
        if(cookies!=null && cookies.length>0){
            Arrays.stream(cookies).filter(cookie -> Objects.equals(cookie.getName(),key)).findFirst().ifPresent(
                    cookie ->
                            value.set(cookie.getValue())
            );
        }
        if(StringUtils.hasLength(redirecturi)){
            response.sendRedirect(redirecturi+"?token="+value.get());
            return ApiResponse.success("Redirecting to "+redirecturi+" to return cookie");
        }
        return ApiResponse.success(value.get());
    }





}
