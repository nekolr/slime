package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FunctionRepository extends JpaRepository<Function, Long>, JpaSpecificationExecutor {
}
