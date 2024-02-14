package com.jiawa.train.batch.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.jiawa.train.batch.feign.BusinessFeign;
import com.jiawa.train.common.toolkits.LogUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;

import java.util.Date;

@DisallowConcurrentExecution
@RequiredArgsConstructor
public class DailyTrainJob implements Job {

    private final BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LogUtil.info("生成车次数据开始");
        var date = new Date();
        var dateTime = DateUtil.offsetDay(date, 1);
        var offsetDate = dateTime.toJdkDate();
        var result = businessFeign.genDaily(offsetDate);
        LogUtil.info("生成距离{}的1天后的车次数据结束，结果：{}",offsetDate,result);
    }
}
