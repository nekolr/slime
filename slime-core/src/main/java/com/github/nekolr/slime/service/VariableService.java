package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Variable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VariableService {

    void removeById(Long id);

    Variable getById(Long id);

    Variable save(Variable entity);

    void update(String variableName, String VariableValue);

    Page<Variable> findAll(Pageable pageable);

}
