package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.io.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface SpiderFlowService {

    SpiderFlow getById(Long id);

    void executeCountIncrement(Long id, Date lastExecuteTime, Date nextExecuteTime);

    void clearNextExecuteTime();

    List<SpiderFlow> findByJobEnabled(Boolean jobEnabled);

    SpiderFlow save(SpiderFlow flow);

    void updateNextExecuteTime(SpiderFlow flow);

    void start(Long id);

    Page<SpiderFlow> findAll(SpiderFlow flow, Pageable pageable);

    List<SpiderFlow> findAll();

    List<SpiderFlow> findOtherFlows(Long id);

    void removeById(Long id);

    void run(Long id);

    List<String> getRecentTriggerTime(String cron, int numTimes);

    void updateCronAndNextExecuteTime(Long id, String cron);

    void stop(Long id);

    List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed, Boolean matchCase, Boolean regex);

}
