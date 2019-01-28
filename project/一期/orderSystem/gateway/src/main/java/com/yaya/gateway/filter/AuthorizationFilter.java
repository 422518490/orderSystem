package com.yaya.gateway.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description 权限过滤
 */
public class AuthorizationFilter implements GlobalFilter,Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (predicate(exchange)){
            // 进行权限验证
            String token = request.getHeaders().get("token").get(0);
        }
        return chain.filter(exchange);
    }

    /**
     * 值越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return -200;
    }

    public Boolean predicate(ServerWebExchange serverWebExchange) {
        URI uri = serverWebExchange.getRequest().getURI();
        String requestUri = uri.getPath();
        String authorization = serverWebExchange.getRequest().getHeaders().getFirst("authorization");
        if (StringUtils.isBlank(authorization)) {
            return false;
        }
        if (isLogoutUrl(requestUri)) {
            return false;
        }
        return true;
    }

    private boolean isLogoutUrl(String url) {
        return url.contains("/login/logout");
    }
}
