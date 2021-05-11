package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * 爬虫流程实体类
 */
@Table(name = "slime_sp_flow")
@Entity
@Getter
@Setter
@ToString
public class SpiderFlow {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 定时任务表达式
     */
    private String cron;

    /**
     * 流程图结构数据
     */
    @Column(columnDefinition = "longtext")
    private String xml;

    /**
     * 执行次数
     */
    @Column(name = "execute_count", insertable = false)
    @ColumnDefault("0")
    private Integer executeCount;

    /**
     * 正在运行的任务数
     */
    @Transient
    private Integer runningCount;

    /**
     * 是否开启定时任务
     */
    @Column(name = "job_enabled", insertable = false)
    @ColumnDefault("false")
    private Boolean jobEnabled;

    /**
     * 创建时间
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;

    /**
     * 上一次执行时间
     */
    @Column(name = "last_execute_time")
    private Date lastExecuteTime;

    /**
     * 下一次执行时间
     */
    @Column(name = "next_execute_time")
    private Date nextExecuteTime;
}
