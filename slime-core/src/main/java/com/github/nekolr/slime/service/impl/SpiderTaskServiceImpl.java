package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.SpiderTaskRepository;
import com.github.nekolr.slime.domain.SpiderTask;
import com.github.nekolr.slime.service.SpiderTaskService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SpiderTaskServiceImpl implements SpiderTaskService {

    @Resource
    private SpiderTaskRepository spiderTaskRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpiderTask save(SpiderTask task) {
        return spiderTaskRepository.save(task);
    }

    @Override
    public SpiderTask getById(Long id) {
        return spiderTaskRepository.findById(id).orElse(null);
    }

    @Override
    public Page<SpiderTask> findAll(SpiderTask task, Pageable pageable) {
        return spiderTaskRepository.findAll(Example.of(task), pageable);
    }

    @Override
    public Long getMaxTaskIdByFlowId(Long flowId) {
        return spiderTaskRepository.findTaskIdByFlowIdOrderByEndTimeDesc(flowId).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        spiderTaskRepository.deleteById(id);
    }
}
