package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.SpiderTaskRepository;
import com.github.nekolr.slime.domain.SpiderTask;
import com.github.nekolr.slime.service.SpiderTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

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
    public Page<SpiderTask> findAll(Pageable pageable) {
        return spiderTaskRepository.findAll(pageable);
    }

    @Override
    public SpiderTask getById(Long id) {
        return spiderTaskRepository.findById(id).orElse(null);
    }

    @Override
    public Page<SpiderTask> findAll(SpiderTask task, Pageable pageable) {
        return spiderTaskRepository.findAll(new Spec(task), pageable);
    }

    @Override
    public Long getMaxTaskIdByFlowId(Long flowId) {
        return spiderTaskRepository.findTaskIdByFlowId(flowId).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        spiderTaskRepository.deleteById(id);
    }

    /**
     * 条件查询
     */
    class Spec implements Specification<SpiderTask> {

        private SpiderTask task;

        public Spec(SpiderTask task) {
            this.task = task;
        }

        @Override
        public Predicate toPredicate(Root<SpiderTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();
            if (task.getFlowId() != null) {
                predicates.add(cb.equal(root.get("flowId").as(Long.class), task.getFlowId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}
