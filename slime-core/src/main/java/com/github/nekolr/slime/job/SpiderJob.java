package com.github.nekolr.slime.job;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.context.SpiderJobContext;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.domain.SpiderTask;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.service.SpiderTaskService;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SpiderJob extends QuartzJobBean {

    private Spider spider;

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowService spiderFlowService;

    @Autowired
    public void setSpider(Spider spider) {
        this.spider = spider;
    }

    private static Map<Long, SpiderContext> contextMap = new ConcurrentHashMap<>();

    @Override
    protected void executeInternal(JobExecutionContext context) {
        if (spiderConfig.getJobEnabled()) {
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            SpiderFlow spiderFlow = (SpiderFlow) jobDataMap.get(Constants.QUARTZ_SPIDER_FLOW_PARAM_NAME);
            run(spiderFlow, context.getNextFireTime());
        }
    }

    /**
     * 执行流程
     *
     * @param flowId 流程 ID
     */
    public void run(Long flowId) {
        run(spiderFlowService.getById(flowId), null);
    }

    /**
     * 执行流程
     *
     * @param flow     流程
     * @param nextTime 下一次执行的时间
     */
    private void run(SpiderFlow flow, Date nextTime) {
        // 当前时间
        Date now = new Date();
        // 创建一个流程任务
        SpiderTask task = createSpiderTask(flow.getId());
        // 创建执行上下文
        SpiderJobContext context = null;
        try {
            context = SpiderJobContext.create(spiderConfig.getWorkspace(), flow.getId(), task.getId(), false);
            log.info("流程：{} 开始执行，任务 ID 为：{}", flow.getName(), task.getId());
            SpiderContextHolder.set(context);
            contextMap.put(task.getId(), context);
            spider.run(flow, context);
            log.info("流程：{} 执行完毕，任务 ID 为：{}，下次执行时间：{}", flow.getName(), task.getId(), TimeUtils.format(nextTime));
        } catch (FileNotFoundException e) {
            log.error("创建日志文件失败", e);
        } catch (Throwable t) {
            log.error("流程：{} 执行出错，任务 ID 为：{}", flow.getName(), task.getId());
        } finally {
            // 关闭流
            if (context != null) {
                context.close();
            }
            contextMap.remove(task.getId());
            SpiderContextHolder.remove();
            // 更新任务结束时间
            task.setEndTime(new Date());
            spiderTaskService.save(task);
        }
        spiderFlowService.executeCountIncrement(flow.getId(), now, nextTime);
    }

    /**
     * 创建一个流程任务
     *
     * @param flowId 流程 ID
     * @return 流程任务
     */
    private SpiderTask createSpiderTask(Long flowId) {
        SpiderTask task = new SpiderTask();
        task.setFlowId(flowId);
        task.setBeginTime(new Date());
        spiderTaskService.save(task);
        return task;
    }

    /**
     * 获取执行上下文
     *
     * @param taskId 任务 ID
     * @return 执行上下文
     */
    public static SpiderContext getSpiderContext(Long taskId) {
        return contextMap.get(taskId);
    }
}
