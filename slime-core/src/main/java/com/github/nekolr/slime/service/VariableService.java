package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Variable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VariableService {

    void removeById(Long id);

    void save(Variable entity);

    Page<Variable> findAll(Pageable pageable);

    Variable getById(Long id);
}
