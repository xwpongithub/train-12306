package com.jiawa.train.common.context;

import com.jiawa.train.common.resp.MemberLoginResp;
import com.jiawa.train.common.toolkits.LogUtil;

public class LoginMemberContext {

    private static final ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    public static MemberLoginResp getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginResp member) {
        LoginMemberContext.member.set(member);
    }

    public static void removeMember() {
        LoginMemberContext.member.remove();
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LogUtil.error(e);
            throw e;
        }
    }

}
