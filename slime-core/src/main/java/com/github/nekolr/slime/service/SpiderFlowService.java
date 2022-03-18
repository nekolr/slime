package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.io.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface SpiderFlowService {

    void run(Long id);

    void stop(Long id);

    void start(Long id);

    void removeById(Long id);

    List<SpiderFlow> findAll();

    SpiderFlow getById(Long id);

    void clearNextExecuteTime();

    SpiderFlow save(SpiderFlow flow);

    List<SpiderFlow> findOtherFlows(Long id);

    void updateNextExecuteTime(SpiderFlow flow);

    List<SpiderFlow> findByJobEnabled(Boolean jobEnabled);

    void updateCronAndNextExecuteTime(Long id, String cron);

    List<String> getRecentTriggerTime(String cron, int numTimes);

    Page<SpiderFlow> findAll(SpiderFlow flow, Pageable pageable);

    void executeCountIncrement(Long id, Date lastExecuteTime, Date nextExecuteTime);

    List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed, Boolean matchCase, Boolean regex);

}
