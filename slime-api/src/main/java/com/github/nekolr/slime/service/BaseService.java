package com.github.nekolr.slime.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseService<T> {

    default void removeById(Long id) { }

    default T save(T entity) {
        return null;
    }

    default Page<T> findAll(Pageable pageable) {
        return null;
    }

    default T getById(Long id) {
        return null;
    }

    default void deleteInBatch(List<T> entries) { }
}
