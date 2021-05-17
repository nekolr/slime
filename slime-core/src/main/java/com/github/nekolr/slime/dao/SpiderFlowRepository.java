package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.SpiderFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SpiderFlowRepository extends JpaRepository<SpiderFlow, Long>, JpaSpecificationExecutor<SpiderFlow> {

    List<SpiderFlow> findByJobEnabled(Boolean jobEnabled);

    List<SpiderFlow> findByIdNot(Long flowId);

    @Modifying
    @Query(value = "update SpiderFlow sf set sf.nextExecuteTime = null")
    void clearNextExecuteTime();

    @Modifying
    @Query(value = "update SpiderFlow sf set sf.cron = :cron, sf.nextExecuteTime = :nextExecuteTime where sf.id = :id")
    void updateCronAndNextExecuteTime(@Param("id") Long id, @Param("cron") String cron, @Param("nextExecuteTime") Date fireTimeAfter);

    @Modifying
    @Query("update SpiderFlow sf set sf.jobEnabled = :enabled where sf.id = :id")
    void updateJobEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);

    @Modifying
    @Query(value = "update slime_sp_flow set execute_count = execute_count + 1, last_execute_time = :lastExecuteTime where id = :id", nativeQuery = true)
    void executeCountIncrement(@Param("lastExecuteTime") Date lastExecuteTime, @Param("id") Long id);

    @Modifying
    @Query(value = "update slime_sp_flow set execute_count = execute_count + 1, last_execute_time = ?, next_execute_time = ? where id = ?", nativeQuery = true)
    void executeCountIncrementAndExecuteNextTime(Date lastExecuteTime, Date nextExecuteTime, Long id);

    @Modifying
    @Query(value = "update SpiderFlow set nextExecuteTime = :nextExecuteTime where id = :id")
    void updateNextExecuteTime(@Param("nextExecuteTime") Date nextExecuteTime, @Param("id") Long id);
}
