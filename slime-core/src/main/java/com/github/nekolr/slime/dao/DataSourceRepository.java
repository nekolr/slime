package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSourceRepository extends JpaRepository<DataSource, Long>, JpaSpecificationExecutor {
}
