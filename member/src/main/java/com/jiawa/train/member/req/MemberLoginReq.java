package com.jiawa.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberLoginReq {

    @NotBlank(message = "【手机号不能为空】")
    @Pattern(regexp = "^1\\d{10}$",message = "手机号码格式不正确")
    private String mobile;
    @NotBlank(message = "【短信验证码】不能为空")
    private String code;

}
