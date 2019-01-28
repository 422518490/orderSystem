package com.yaya.gateway.limit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description 请求的地址作为限流键
 */
public class RemoteAddressKeyResolver implements KeyResolver{

    public static final String BEAN_NAME = "remoteAddressKeyResolver";

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String address = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
        return Mono.just(address);
    }
}
