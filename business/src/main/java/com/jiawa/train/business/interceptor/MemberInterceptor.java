package com.jiawa.train.business.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.MemberLoginResp;
import com.jiawa.train.common.toolkits.JwtUtil;
import com.jiawa.train.common.toolkits.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
public class MemberInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LogUtil.debug("MemberInterceptor开始");
        //获取header的token参数
        var token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LogUtil.debug("获取会员登录token：{}", token);
            var LogUtilinMember = JwtUtil.getJSONObject(token);
            LogUtil.debug("当前登录会员：{}", LogUtilinMember);
            var member = JSONUtil.toBean(LogUtilinMember, MemberLoginResp.class);
            LoginMemberContext.setMember(member);
        }
        LogUtil.info("MemberInterceptor结束");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginMemberContext.removeMember();
    }


}
