package com.github.nekolr.slime.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
public class UserConfig {

    /**
     * 用户名
     */
    @Value("${spider.username}")
    private String username;

    /**
     * 密码
     */
    @Value("${spider.password}")
    private String password;

}
