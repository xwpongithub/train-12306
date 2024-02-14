package com.jiawa.train.member.service;

import com.jiawa.train.common.resp.MemberLoginResp;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.req.MemberLoginReq;

public interface IMemberService {

    long register(MemberRegisterReq req);

    void sendCode(MemberSendCodeReq req);

    MemberLoginResp login(MemberLoginReq req);

}
