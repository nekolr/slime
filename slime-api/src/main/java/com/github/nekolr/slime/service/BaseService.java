package com.github.nekolr.slime.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T> {

    void removeById(Long id);

    T save(T entity);

    Page<T> findAll(Pageable pageable);

    T getById(Long id);
}
