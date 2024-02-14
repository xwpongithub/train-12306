package com.jiawa.train.business.advice;

import cn.hutool.core.util.StrUtil;
import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.annotation.IgnoreResponseSerializable;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return !(
                methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseSerializable.class) ||
                        (methodParameter.getMethod() !=null && methodParameter.getMethod().isAnnotationPresent(IgnoreResponseSerializable.class))
                        || StrUtil.containsAny(methodParameter.getDeclaringClass().getName(),"WebMvcEndpointHandlerMapping")
        );
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest req, ServerHttpResponse res) {
        JsonResult r;
        if (body == null) {
            r = JsonResult.ok();
        } else if (body instanceof JsonResult jsonResult) {
            r = jsonResult;
        } else {
            if (body instanceof LinkedHashMap){
                // 如果返回值是500或者404
                Map<String,Object> errorResult = (Map<String,Object>)body;
                String statusKey = "status";
                String errorKey ="error";
                if (errorResult.containsKey(statusKey)&& errorResult.containsKey(errorKey)
                        && errorResult.containsKey("path")&&errorResult.containsKey("timestamp")){
                    int status = (int)errorResult.get(statusKey);
                    String msg = "";
                    if (status == 404) {
                        msg= "没有找到相应内容";
                    } else if (status == 500) {
                        msg = "服务异常，请联系管理员";
                    }
                    r = JsonResult.error(msg).setCode(status);
                } else {
                    r = JsonResult.error("服务异常，请联系管理员").setCode(500);
                }
            } else {
                r = JsonResult.content(body);
            }
        }
        if (r.getContent() == null) {
            r.remove("content");
        }
        return r;
    }

}
