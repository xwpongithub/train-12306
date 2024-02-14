package com.jiawa.train.member.controller;

import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.resp.MemberLoginResp;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.req.MemberLoginReq;
import com.jiawa.train.member.service.IMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final IMemberService iMemberService;

    @PostMapping("register")
    public JsonResult register(@Valid @RequestBody MemberRegisterReq req) {
        return JsonResult.content(iMemberService.register(req)).setMessage("注册成功");
    }

    @PostMapping("send-code")
    public void sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        iMemberService.sendCode(req);
    }

    @PostMapping("login")
    public MemberLoginResp login(@Valid @RequestBody MemberLoginReq req) {
        return iMemberService.login(req);
    }

}
