package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {

    void removeById(Long id);

    Function getById(Long id);

    Function save(Function function);

    Page<Function> findAll(Pageable pageable);

    Page<Function> findAll(Function entity, Pageable pageable);
}
