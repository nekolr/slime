package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.FunctionRepository;
import com.github.nekolr.slime.domain.Function;
import com.github.nekolr.slime.script.ScriptManager;
import com.github.nekolr.slime.service.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptEngine;

@Service
@Slf4j
public class FunctionServiceImpl implements FunctionService {

    @Resource
    private FunctionRepository functionRepository;

    /**
     * 初始化或者重置自定义函数
     */
    @PostConstruct
    private void initializeFunctions() {
        try {
            ScriptManager.lock();
            ScriptManager.clearFunctions();
            ScriptEngine engine = ScriptManager.createEngine();
            functionRepository.findAll().forEach(function ->
                    ScriptManager.registerFunction(engine, function.getName(), function.getParameter(), function.getScript()));
            ScriptManager.setScriptEngine(engine);
        } finally {
            ScriptManager.unlock();
        }
    }

    @Override
    public Page<Function> findAll(Function entity, Pageable pageable) {
        return functionRepository.findAll(new Spec(entity), pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Function function) {
        try {
            ScriptManager.validScript(function.getName(), function.getParameter(), function.getScript());
            functionRepository.save(function);
            // 重新加载自定义函数
            initializeFunctions();
        } catch (Exception e) {
            throw new RuntimeException("自定义函数不符合规范");
        }
    }

    @Override
    public Function getById(Long id) {
        return functionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        functionRepository.deleteById(id);
    }

    /**
     * 条件查询
     */
    class Spec implements Specification<Function> {

        private Function function;

        public Spec(Function function) {
            this.function = function;
        }

        @Override
        public Predicate toPredicate(Root<Function> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            if (StringUtils.isNotBlank(function.getName())) {
                return cb.and(cb.like(root.get("name").as(String.class), "%" + function.getName() + "%"));
            }
            return cb.and(new Predicate[0]);
        }
    }
}
