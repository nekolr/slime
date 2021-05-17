package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.dto.DataSourceDTO;

import java.util.List;

public interface DataSourceService extends BaseService<DataSourceDTO> {

    List<DataSourceDTO> findAll();

    void test(DataSourceDTO dataSource);
}
