package com.jiawa.train.common.toolkits;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class LogUtil {

    public static void warn(String warnInfo,Object ...msgs){
        if (log.isWarnEnabled()){
            log.warn(warnInfo,msgs);
        }
    }

    public static void debug(String debugInfo,Object... msgs){
        if (log.isDebugEnabled()){
            log.debug(debugInfo,msgs);
        }
    }

    public static void error(Throwable e) {
        if (log.isErrorEnabled() && Objects.nonNull(e)){
            log.error(e.getMessage(), e);
        }
    }

    public static void info(String info,Object... msgs) {
        if (log.isInfoEnabled()){
            log.info(info,msgs);
        }
    }

}
