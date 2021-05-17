package com.github.nekolr.slime.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
public class ProxyDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * IP 地址
     */
    @NotBlank(message = "代理的 IP 地址不能为空")
    private String ip;

    /**
     * 端口号
     */
    @NotBlank(message = "代理的端口号不能为空")
    private Integer port;

    /**
     * 类型
     */
    @NotBlank(message = "代理的类型不能为空")
    private String type;

    /**
     * 是否高匿
     */
    @ColumnDefault("false")
    private Boolean anonymous;

    /**
     * 验证时间
     */
    private Date validTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyDTO proxyDTO = (ProxyDTO) o;
        return Objects.equals(id, proxyDTO.id) && Objects.equals(ip, proxyDTO.ip) && Objects.equals(port, proxyDTO.port) && Objects.equals(type, proxyDTO.type) && Objects.equals(anonymous, proxyDTO.anonymous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port, type, anonymous);
    }
}