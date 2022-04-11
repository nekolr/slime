package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.SpiderTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpiderTaskService {

    void removeById(Long id);

    SpiderTask getById(Long id);

    SpiderTask save(SpiderTask task);

    Long getMaxTaskIdByFlowId(Long flowId);

    Integer getRunningCountByFlowId(Long flowId);

    Page<SpiderTask> findAll(SpiderTask task, Pageable pageable);
}
