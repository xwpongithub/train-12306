package com.jiawa.train.common.exception;

import lombok.Getter;

@Getter
public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST(5001,"手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST(5002,"请先获取短信验证码"),
    MEMBER_MOBILE_CODE_ERROR(5003,"短信验证码错误"),

    USINESS_STATION_NAME_UNIQUE_ERROR(6001,"车站已存在"),
    BUSINESS_TRAIN_CODE_UNIQUE_ERROR(6002,"车次编号已存在"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR(6003,"同车次站序已存在"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR(6004,"同车次站名已存在"),
    BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR(6005,"同车次厢号已存在"),

    CONFIRM_ORDER_TICKET_COUNT_ERROR(6006,"余票不足"),
    CONFIRM_ORDER_EXCEPTION(6007,"服务器忙，请稍候重试"),
    CONFIRM_ORDER_LOCK_FAIL(6008,"当前抢票人数过多，请稍候重试"),
    CONFIRM_ORDER_FLOW_EXCEPTION(6009,"当前抢票人数太多了，请稍候重试"),
    CONFIRM_ORDER_SK_TOKEN_FAIL(6010,"当前抢票人数过多，请5秒后重试");

    private final int code;
    private final String desc;

    BusinessExceptionEnum(int code,String desc) {
        this.code = code;
        this.desc = desc;
    }

}
