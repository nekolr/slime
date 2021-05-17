package com.github.nekolr.slime.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Getter
public class ProxyConfig {

    /**
     * 检测代理是否有效时使用的地址
     */
    @Value("${spider.proxy.check-url}")
    private String checkUrl;

    /**
     * 检测代理是否有效的超时时间，单位毫秒
     */
    @Value("${spider.proxy.check-timeout}")
    private Integer checkTimeout;

    /**
     * 检测时间间隔
     */
    @Value("${spider.proxy.check-interval}")
    private Duration checkInterval;
}
