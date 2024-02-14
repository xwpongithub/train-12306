package com.jiawa.train.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {
        var app = SpringApplication.run(GatewayApplication.class, args);
        var envs = app.getEnvironment();
        log.info("网关模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
    }

}
