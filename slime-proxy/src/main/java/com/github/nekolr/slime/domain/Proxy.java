package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理实体类
 */
@Table(name = "slime_sp_proxy")
@Entity
@Getter
@Setter
@ToString
public class Proxy {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 类型
     */
    private String type;

    /**
     * 是否高匿
     */
    private Boolean anonymous;

    /**
     * 验证时间
     */
    @Column(name = "valid_time", insertable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date validTime;
}
