package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.dao.SpiderFlowRepository;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.io.Line;
import com.github.nekolr.slime.io.RandomAccessFileReader;
import com.github.nekolr.slime.job.SpiderJobManager;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.service.SpiderTaskService;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

@Service
@Slf4j
public class SpiderFlowServiceImpl implements SpiderFlowService {

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderJobManager spiderJobManager;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowRepository spiderFlowRepository;

    @Autowired
    @SuppressWarnings("all")
    private PlatformTransactionManager txManager;

    /**
     * 项目启动后自动添加需要执行的定时任务
     */
    @PostConstruct
    public void initializeJobs() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            // 保证 doInTransactionWithoutResult 方法里的代码在事务中
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // 清空所有流程的下次执行时间
                clearNextExecuteTime();
                // 获取所有启用定时任务的流程
                List<SpiderFlow> flows = findByJobEnabled(Boolean.TRUE);
                if (flows != null && !flows.isEmpty()) {
                    for (SpiderFlow flow : flows) {
                        if (StringUtils.isNotBlank(flow.getCron())) {
                            Date nextTime = spiderJobManager.addJob(flow);
                            log.info("初始化定时任务：{}，下次执行时间：{}", flow.getName(), TimeUtils.format(nextTime));
                            if (nextTime != null) {
                                flow.setNextExecuteTime(nextTime);
                                updateNextExecuteTime(flow);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public SpiderFlow getById(Long id) {
        return spiderFlowRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeCountIncrement(Long id, Date lastExecuteTime, Date nextExecuteTime) {
        if (nextExecuteTime == null) {
            spiderFlowRepository.executeCountIncrement(lastExecuteTime, id);
        } else {
            spiderFlowRepository.executeCountIncrementAndExecuteNextTime(lastExecuteTime, nextExecuteTime, id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearNextExecuteTime() {
        spiderFlowRepository.clearNextExecuteTime();
    }

    @Override
    public List<SpiderFlow> findByJobEnabled(Boolean jobEnabled) {
        return spiderFlowRepository.findByJobEnabled(jobEnabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpiderFlow save(SpiderFlow flow) {
        if (flow.getId() != null) {
            Optional<SpiderFlow> entity = spiderFlowRepository.findById(flow.getId());
            if (entity.isPresent()) {
                // 以下字段需要回填
                flow.setCron(entity.get().getCron());
                flow.setJobEnabled(entity.get().getJobEnabled());
                flow.setExecuteCount(entity.get().getExecuteCount());
                flow.setLastExecuteTime(entity.get().getLastExecuteTime());
                // 如果任务正在执行，则设置下次执行时间
                if (StringUtils.isNotBlank(flow.getCron()) && flow.getJobEnabled()) {
                    CronTrigger trigger = TriggerBuilder.newTrigger()
                            .withSchedule(CronScheduleBuilder.cronSchedule(flow.getCron()))
                            .build();
                    flow.setNextExecuteTime(trigger.getFireTimeAfter(null));
                    // 重新发布任务
                    if (spiderJobManager.removeJob(flow.getId())) {
                        spiderJobManager.addJob(flow);
                    }
                } else {
                    flow.setNextExecuteTime(entity.get().getNextExecuteTime());
                }
            }
        }
        return spiderFlowRepository.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNextExecuteTime(SpiderFlow flow) {
        spiderFlowRepository.updateNextExecuteTime(flow.getNextExecuteTime(), flow.getId());
    }

    @Override
    public Page<SpiderFlow> findAll(SpiderFlow flow, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.CONTAINING));
        return spiderFlowRepository.findAll(Example.of(flow, matcher), pageable);
    }

    @Override
    public List<SpiderFlow> findAll() {
        return spiderFlowRepository.findAll();
    }

    @Override
    public List<SpiderFlow> findOtherFlows(Long id) {
        return spiderFlowRepository.findByIdNot(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        spiderFlowRepository.deleteById(id);
    }

    @Override
    public void run(Long id) {
        spiderJobManager.run(id);
    }

    @Override
    public List<String> getRecentTriggerTime(String cron, int numTimes) {
        List<String> list = new ArrayList<>();
        CronTrigger trigger;
        try {
            trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        } catch (Exception e) {
            throw new RuntimeException("cron 表达式 " + cron + " 有误");
        }
        List<Date> dates = TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, numTimes);
        for (Date date : dates) {
            list.add(TimeUtils.format(date));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCronAndNextExecuteTime(Long id, String cron) {
        // 创建触发器
        CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        // 删除定时任务
        if (spiderJobManager.removeJob(id)) {
            // 计算下次执行时间后，更新流程
            spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, trigger.getFireTimeAfter(null));
            SpiderFlow flow = getById(id);
            // 定时任务已开启
            if (flow.getJobEnabled()) {
                // 添加任务
                spiderJobManager.addJob(flow);
            } else {
                spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, null);
            }
        } else {
            spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        // 先尝试删除任务
        if (spiderJobManager.removeJob(id)) {
            // 设置定时任务状态为开启
            spiderFlowRepository.updateJobEnabled(id, Boolean.TRUE);
            SpiderFlow flow = getById(id);
            if (flow != null) {
                // 添加任务
                Date nextExecuteTime = spiderJobManager.addJob(flow);
                if (nextExecuteTime != null) {
                    // 更新下次执行时间
                    flow.setNextExecuteTime(nextExecuteTime);
                    spiderFlowRepository.updateNextExecuteTime(flow.getNextExecuteTime(), flow.getId());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stop(Long id) {
        spiderFlowRepository.updateJobEnabled(id, Boolean.FALSE);
        spiderFlowRepository.updateNextExecuteTime(null, id);
        spiderJobManager.removeJob(id);
    }

    @Override
    public List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed, Boolean matchCase, Boolean regex) {

        if (Objects.isNull(taskId)) {
            Long maxId = spiderTaskService.getMaxTaskIdByFlowId(id);
            if (Objects.isNull(maxId)) {
                throw new RuntimeException("该流程没有运行过的任务");
            } else {
                taskId = maxId;
            }
        }

        List<Line> lines;
        String flowFolder = Constants.SPIDER_FLOW_LOG_DIR_PREFIX + id;
        String taskFolder = Constants.SPIDER_TASK_LOG_DIR_PREFIX + taskId;
        File logFile = new File(new File(spiderConfig.getWorkspace()), "logs" + File.separator + flowFolder + File.separator + "logs" + File.separator + taskFolder + ".log");

        try (RandomAccessFileReader reader = new RandomAccessFileReader(new RandomAccessFile(logFile, "r"), index == null ? -1 : index, reversed == null || reversed)) {
            lines = reader.readLine(count == null ? 10 : count, keywords, matchCase != null && matchCase, regex != null && regex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("日志文件不存在", e);
        } catch (IOException e) {
            throw new RuntimeException("读取日志文件出错", e);
        }
        return lines;
    }
}
