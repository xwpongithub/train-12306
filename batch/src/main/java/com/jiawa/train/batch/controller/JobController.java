package com.jiawa.train.batch.controller;

import com.jiawa.train.batch.req.CronJobReq;
import com.jiawa.train.batch.resp.CronJobResp;
import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.toolkits.LogUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("admin/job")
@RequiredArgsConstructor
public class JobController {

    private final SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping("run")
    public void run(@RequestBody CronJobReq cronJobReq) throws SchedulerException {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LogUtil.info("手动执行任务开始：{}, {}", jobClassName, jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
    }

    @RequestMapping("add")
    public JsonResult add(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LogUtil.info("创建定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        JsonResult commonResp;
        try {
            // 通过SchedulerFactory获取一个调度器实例
            Scheduler sched = schedulerFactoryBean.getScheduler();
            // 启动调度器
            sched.start();
            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();
            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();
            sched.scheduleJob(jobDetail, trigger);
            commonResp = JsonResult.ok();
        } catch (SchedulerException e) {
            LogUtil.error(e);
            commonResp = JsonResult.error("创建定时任务失败:调度异常");
        } catch (ClassNotFoundException e) {
            LogUtil.error(e);
            commonResp = JsonResult.error("创建定时任务失败：任务类不存在");
        }
        LogUtil.info("创建定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping("pause")
    public JsonResult pause(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LogUtil.info("暂停定时任务开始：{}，{}", jobClassName, jobGroupName);
        JsonResult commonResp;
        try {
            commonResp = JsonResult.ok();
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LogUtil.error(e);
            commonResp = JsonResult.error("暂停定时任务失败:调度异常");
        }
        LogUtil.info("暂停定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping( "resume")
    public JsonResult resume(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LogUtil.info("重启定时任务开始：{}，{}", jobClassName, jobGroupName);
        JsonResult commonResp;
        try {
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
            commonResp = JsonResult.ok();
        } catch (SchedulerException e) {
            LogUtil.error(e);
            commonResp =JsonResult.error("重启定时任务失败:调度异常");
        }
        LogUtil.info("重启定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping("reschedule")
    public JsonResult reschedule(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LogUtil.info("更新定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        JsonResult commonResp;
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger1.setStartTime(new Date()); // 重新设置开始时间
            CronTrigger trigger = trigger1;

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            commonResp = JsonResult.ok();
        } catch (Exception e) {
            LogUtil.error(e);
            commonResp= JsonResult.error("更新定时任务失败:调度异常");
        }
        LogUtil.info("更新定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping("delete")
    public JsonResult delete(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LogUtil.info("删除定时任务开始：{}，{}", jobClassName, jobGroupName);
        JsonResult commonResp;
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
            commonResp= JsonResult.ok();
            LogUtil.info("删除定时任务结束：{}", commonResp);
        } catch (SchedulerException e) {
            LogUtil.error(e);
            commonResp= JsonResult.error("删除定时任务失败:调度异常");
        }
        LogUtil.info("删除定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping("query")
    public JsonResult query() {
        LogUtil.info("查看所有定时任务开始");
        JsonResult commonResp;
        List<CronJobResp> cronJobDtoList = new ArrayList();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobResp cronJobResp = new CronJobResp();
                    cronJobResp.setName(jobKey.getName());
                    cronJobResp.setGroup(jobKey.getGroup());

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobResp.setCronExpression(cronTrigger.getCronExpression());
                    cronJobResp.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    cronJobResp.setState(triggerState.name());

                    cronJobDtoList.add(cronJobResp);
                }

            }
            commonResp = JsonResult.ok();
            commonResp.setContent(cronJobDtoList);
        } catch (SchedulerException e) {
            LogUtil.error( e);
            commonResp = JsonResult.error("查看定时任务失败:调度异常");
        }
        LogUtil.info("查看定时任务结束：{}", commonResp);
        return commonResp;
    }

}
