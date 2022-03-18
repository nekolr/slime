package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.SpiderTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpiderTaskRepository extends JpaRepository<SpiderTask, Long>, JpaSpecificationExecutor<SpiderTask> {

    @Query(value = "select id from SpiderTask where flowId = :flowId order by endTime desc")
    List<Long> findTaskIdByFlowIdOrderByEndTimeDesc(@Param("flowId") Long flowId);

}
