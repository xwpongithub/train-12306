package com.jiawa.train.business.enums;

import lombok.Getter;

@Getter
public enum ConfirmOrderStatusEnum {

    INIT("I", "初始"),
    PENDING("P", "处理中"),
    SUCCESS("S", "成功"),
    FAILURE("F", "失败"),
    EMPTY("E", "无票"),
    CANCEL("C", "取消");

    private final String code;

    private final String desc;

    ConfirmOrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
