package com.jiawa.train.common.toolkits;

import cn.hutool.core.util.IdUtil;

public class SnowflakeUtil {

    /**
     * 数据中心id（比如北京，上海）
     */
    private static final long dataCenterId = 1;
    /**
     * 某个数据中心下的某台机器的id
     */
    private static final long workerId = 1;

    public static long getSnowflakeId() {
       return IdUtil.getSnowflake(workerId,dataCenterId).nextId();
    }

    public static String getSnowflakeIdStr() {
        return IdUtil.getSnowflake(workerId,dataCenterId).nextIdStr();
    }
}
