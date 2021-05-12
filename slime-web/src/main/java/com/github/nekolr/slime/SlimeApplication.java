package com.github.nekolr.slime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
@EnableScheduling
public class SlimeApplication implements ServletContextInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SlimeApplication.class, args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 设置文本缓存 1MB
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", Integer.toString((1024 * 1024)));
    }
}
