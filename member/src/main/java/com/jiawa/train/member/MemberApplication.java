package com.jiawa.train.member;

import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MemberApplication {

    public static void main(String[] args) {

        var app = SpringApplication.run(MemberApplication.class,args);
        var envs = app.getEnvironment();
        LogUtil.info("会员模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
    }

}
