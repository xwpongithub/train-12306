package com.jiawa.train.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PassengerSaveReq {

    private Long id;
    /**
     * 姓名
     */
    @NotBlank(message = "【姓名】不能为空")
    private String name;

    /**
     * 身份证
     */
    @NotBlank(message = "【身份证】不能为空")
    private String idCard;

    /**
     * 旅客类型|枚举[PassengerTypeEnum]
     */
    @NotBlank(message = "【旅客类型】不能为空")
    private String type;

}
