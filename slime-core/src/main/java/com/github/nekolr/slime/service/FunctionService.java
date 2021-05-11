package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {

    Page<Function> findAll(Function entity, Pageable pageable);

    void save(Function function);

    Function getById(Long id);

    void removeById(Long id);
}
