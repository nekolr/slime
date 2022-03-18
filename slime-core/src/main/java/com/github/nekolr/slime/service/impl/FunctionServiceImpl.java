package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.FunctionRepository;
import com.github.nekolr.slime.domain.Function;
import com.github.nekolr.slime.script.ScriptManager;
import com.github.nekolr.slime.service.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.CONTAINING));
        return functionRepository.findAll(Example.of(entity, matcher), pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function save(Function function) {
        try {
            ScriptManager.validScript(function.getName(), function.getParameter(), function.getScript());
            Function entity = functionRepository.save(function);
            // 重新加载自定义函数
            initializeFunctions();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("自定义函数不符合规范");
        }
    }

    @Override
    public Page<Function> findAll(Pageable pageable) {
        return functionRepository.findAll(pageable);
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
}
