package com.jiawa.train.business;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.ArrayList;

@SpringBootApplication
@EnableFeignClients("com.jiawa.train.business.feign")
public class BusinessApplication {

    public static void main(String[] args) {
        var app = SpringApplication.run(BusinessApplication.class, args);
        var envs = app.getEnvironment();
        LogUtil.info("售票模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
        initFlowRules();
    }

    private static void initFlowRules() {
        var rules = new ArrayList<FlowRule>();
        var rule = new FlowRule();
        rule.setResource("doConfirmOrder");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

}
