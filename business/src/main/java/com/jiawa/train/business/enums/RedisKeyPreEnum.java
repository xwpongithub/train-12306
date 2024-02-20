package com.jiawa.train.business.enums;

import lombok.Getter;

@Getter
public enum RedisKeyPreEnum {

    CONFIRM_ORDER("LOCK_CONFIRM_ORDER", "购票锁"),
    SK_TOKEN("LOCK_SK_TOKEN", "令牌锁"),
    SK_TOKEN_COUNT("SK_TOKEN_COUNT", "令牌数");

    private final String code;

    private final String desc;

    RedisKeyPreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
