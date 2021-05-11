package com.github.nekolr.slime.job;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class SpiderJobManager {

    @Resource
    private Spider spider;

    @Resource
    private SpiderJob spiderJob;

    @Resource
    private Scheduler quartzScheduler;

    @Resource
    private SpiderFlowService spiderFlowService;

//    /**
//     * 项目启动后自动添加需要执行的定时任务
//     */
//    @PostConstruct
//    private void initializeJobs() {
//        // 清空所有流程的下次执行时间
//        spiderFlowService.clearNextExecuteTime();
//        // 获取所有启用定时任务的流程
//        List<SpiderFlow> flows = spiderFlowService.findByJobEnabled(Boolean.TRUE);
//        if (flows != null && !flows.isEmpty()) {
//            for (SpiderFlow flow : flows) {
//                if (StringUtils.isNotBlank(flow.getCron())) {
//                    Date nextTime = addJob(flow);
//                    log.info("初始化定时任务：{}，下次执行时间：{}", flow.getName(), TimeUtils.format(nextTime));
//                    if (nextTime != null) {
//                        flow.setNextExecuteTime(nextTime);
//                        spiderFlowService.updateNextExecuteTime(flow);
//                    }
//                }
//            }
//        }
//    }

    /**
     * 创建定时任务
     *
     * @param flow 流程
     * @return 下次执行时间
     */
    public Date addJob(SpiderFlow flow) {
        try {
            // 构建任务
            JobDetail job = JobBuilder.newJob(SpiderJob.class).withIdentity(getJobKey(flow.getId())).build();
            job.getJobDataMap().put(Constants.QUARTZ_SPIDER_FLOW_PARAM_NAME, flow);
            // 设置触发时间
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(flow.getCron())
                    .withMisfireHandlingInstructionDoNothing();
            // 创建触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(flow.getId())).withSchedule(cronScheduleBuilder).build();

            return quartzScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("创建定时任务出错", e);
            return null;
        }
    }

    /**
     * 直接运行
     *
     * @param flowId 流程 ID
     */
    public void run(Long flowId) {
        spider.getThreadPool().submit(() -> spiderJob.run(flowId));
    }

    /**
     * 删除定时任务
     *
     * @param flowId 流程 ID
     * @return 是否成功
     */
    public boolean removeJob(Long flowId) {
        try {
            quartzScheduler.pauseTrigger(getTriggerKey(flowId));
            quartzScheduler.unscheduleJob(getTriggerKey(flowId));
            quartzScheduler.deleteJob(getJobKey(flowId));
            return true;
        } catch (SchedulerException e) {
            log.error("删除定时任务失败", e);
            return false;
        }
    }

    /**
     * 获取 JobKey
     *
     * @param flowId 流程 ID
     * @return JobKey
     */
    private JobKey getJobKey(Long flowId) {
        return JobKey.jobKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }

    /**
     * 获取 TriggerKey
     *
     * @param flowId 流程 ID
     * @return TriggerKey
     */
    private TriggerKey getTriggerKey(Long flowId) {
        return TriggerKey.triggerKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }
}
