package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.DataSourceRepository;
import com.github.nekolr.slime.domain.DataSource;
import com.github.nekolr.slime.domain.dto.DataSourceDTO;
import com.github.nekolr.slime.domain.mapper.DataSourceMapper;
import com.github.nekolr.slime.service.DataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Resource
    private DataSourceRepository dataSourceRepository;

    @Resource
    private DataSourceMapper mapper;

    @Override
    public DataSourceDTO getById(Long id) {
        Optional<DataSource> optional = dataSourceRepository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDto(optional.get());
        }
        return null;
    }

    @Override
    public Page<DataSourceDTO> findAll(Pageable pageable) {
        return dataSourceRepository.findAll(pageable).map(e -> mapper.toDto(e));
    }

    @Override
    public List<DataSourceDTO> findAll() {
        return mapper.toDto(dataSourceRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSourceDTO save(DataSourceDTO dataSource) {
        return mapper.toDto(dataSourceRepository.save(mapper.toEntity(dataSource)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        dataSourceRepository.deleteById(id);
    }

    @Override
    public void test(DataSourceDTO dataSource) {
        Connection connection = null;
        try {
            Class.forName(dataSource.getDriverClassName());
            String url = dataSource.getJdbcUrl();
            String username = dataSource.getUsername();
            String password = dataSource.getPassword();
            if (StringUtils.isNotBlank(username)) {
                connection = DriverManager.getConnection(url, username, password);
            } else {
                connection = DriverManager.getConnection(url);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到驱动包：" + dataSource.getDriverClassName());
        } catch (Exception e) {
            throw new RuntimeException("连接失败，" + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
