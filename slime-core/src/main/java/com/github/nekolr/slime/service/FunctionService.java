package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService extends BaseService<Function> {

    Page<Function> findAll(Function entity, Pageable pageable);
}
