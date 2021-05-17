package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VariableRepository extends JpaRepository<Variable, Long>, JpaSpecificationExecutor<Variable> {
}
