package com.jiawa.train.member.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.MemberLoginResp;
import com.jiawa.train.common.toolkits.JwtUtil;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.member.entity.Member;
import com.jiawa.train.member.mapper.MemberMapper;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.req.MemberLoginReq;
import com.jiawa.train.member.service.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements IMemberService {

    private final MemberMapper memberMapper;

    @Override
    public long register(MemberRegisterReq req) {
        var mobile = req.getMobile();
        var mobileExistsQ = Wrappers.<Member>lambdaQuery();
        mobileExistsQ.eq(Member::getMobile,mobile);
        var mobileExists = memberMapper.exists(mobileExistsQ);
        if (mobileExists) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        var newMember = new Member();
        newMember.setMobile(mobile);
        memberMapper.insert(newMember);
        return newMember.getId();
    }


    @Override
    public void sendCode(MemberSendCodeReq req) {
        var mobile = req.getMobile();
        var mobileExistsQ = Wrappers.<Member>lambdaQuery();
        mobileExistsQ.eq(Member::getMobile,mobile);
        var mobileExists = memberMapper.exists(mobileExistsQ);
        if (!mobileExists) {
            // 手机号未注册则直接注册
            var newMember = new Member();
            newMember.setMobile(mobile);
            memberMapper.insert(newMember);
        }
        // 生成验证码
//        var code = RandomUtil.randomNum4();
        var code = "8888";
        LogUtil.info("生成短信验证码:{}",code);
        // 保存短信记录表 手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        // 对接短信通道，发送短信
    }

    @Override
    public MemberLoginResp login(MemberLoginReq req) {
        var mobile = req.getMobile();
        var code = req.getCode();
        var mobileExistsQ = Wrappers.<Member>lambdaQuery();
        mobileExistsQ.eq(Member::getMobile,mobile);
        var mobileExists = memberMapper.selectOne(mobileExistsQ);
        if (Objects.isNull(mobileExists)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        // 校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        var memberLoginResp = new MemberLoginResp();
        BeanUtils.copyProperties(req,memberLoginResp);

        var token = JwtUtil.createToken(mobileExists.getId(),mobile);
        memberLoginResp.setToken(token);
        memberLoginResp.setId(mobileExists.getId());
        return memberLoginResp;
    }

}
