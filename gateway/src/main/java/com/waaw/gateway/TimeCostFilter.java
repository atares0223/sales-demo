package com.waaw.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TimeCostFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        String uri = httpRequest.getURI().toString();
        long start = System.currentTimeMillis();
        log.info("请求开始时间 {} , URI: {}", start, uri);
        return chain.filter(exchange).doFinally(result->{
            long end = System.currentTimeMillis();
            log.info("请求结束时间 {} , URI: {} , 总耗时： {} ms", end, uri, (end - start));
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
