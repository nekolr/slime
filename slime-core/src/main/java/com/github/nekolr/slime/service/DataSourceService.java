package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.dto.DataSourceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataSourceService {

    void removeById(Long id);

    List<DataSourceDTO> findAll();

    DataSourceDTO getById(Long id);

    void test(DataSourceDTO dataSource);

    DataSourceDTO save(DataSourceDTO dataSource);

    Page<DataSourceDTO> findAll(Pageable pageable);
}
