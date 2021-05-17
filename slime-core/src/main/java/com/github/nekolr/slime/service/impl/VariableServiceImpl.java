package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.VariableRepository;
import com.github.nekolr.slime.domain.Variable;
import com.github.nekolr.slime.expression.ExpressionGlobalVariables;
import com.github.nekolr.slime.service.VariableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VariableServiceImpl implements VariableService {

    @Resource
    private VariableRepository variableRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        variableRepository.deleteById(id);
        this.resetGlobalVariables();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Variable save(Variable entity) {
        Variable variable = variableRepository.save(entity);
        this.resetGlobalVariables();
        return variable;
    }

    @Override
    public Page<Variable> findAll(Pageable pageable) {
        return variableRepository.findAll(pageable);
    }

    @Override
    public Variable getById(Long id) {
        return variableRepository.findById(id).orElse(null);
    }

    @PostConstruct
    private void resetGlobalVariables() {
        Map<String, String> variables = variableRepository.findAll().stream()
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));
        ExpressionGlobalVariables.reset(variables);
    }
}
