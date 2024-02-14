package com.jiawa.train.batch;

import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.jiawa.train.batch.feign")
public class BatchApplication {

    public static void main(String[] args) {
        var app = SpringApplication.run(BatchApplication.class, args);
        var envs = app.getEnvironment();
        LogUtil.info("定时任务模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
    }

}
