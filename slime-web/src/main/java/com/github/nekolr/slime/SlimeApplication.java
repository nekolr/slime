package com.github.nekolr.slime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 取消 UserDetailsServiceAutoConfiguration 自动配置
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableScheduling
public class SlimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlimeApplication.class, args);
    }

}
