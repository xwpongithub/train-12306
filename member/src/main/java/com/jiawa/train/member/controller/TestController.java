package com.jiawa.train.member.controller;

import com.jiawa.train.common.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RefreshScope
public class TestController {

    @Value("${info.app}")
    private String info;


    @GetMapping("info")
    public JsonResult info() {
        return JsonResult.content(info);
    }
}
