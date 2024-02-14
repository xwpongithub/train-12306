package com.jiawa.train.gateway;

import com.jiawa.train.common.toolkits.JwtUtil;
import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginMemberFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/admin")
                || path.contains("/member/member/login")
                || path.contains("/member/member/send-code")) {
            LogUtil.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        }
        LogUtil.info("需要登录验证：{}", path);
        // 获取header的token参数
        String token = exchange.getRequest().getHeaders().getFirst("token");
        LogUtil.info("会员登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            LogUtil.info( "token为空，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 校验token是否有效，包括token是否被改过，是否过期
        boolean validate = JwtUtil.validate(token);
        if (validate) {
            LogUtil.info("token有效，放行该请求");
            return chain.filter(exchange);
        } else {
            LogUtil.warn( "token无效，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
