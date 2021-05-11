package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.dto.DataSourceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataSourceService {

    DataSourceDTO getById(Long id);

    Page<DataSourceDTO> findAll(Pageable pageable);

    List<DataSourceDTO> findAll();

    void save(DataSourceDTO dataSource);

    void removeById(Long id);

    void test(DataSourceDTO dataSource);
}
