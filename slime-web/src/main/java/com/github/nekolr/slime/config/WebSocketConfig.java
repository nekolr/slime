package com.github.nekolr.slime.config;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.websocket.WebSocketEditorHandler;
import com.github.nekolr.slime.websocket.WebSocketEditorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    public void setSpider(Spider spider) {
        WebSocketEditorHandler.spider = spider;
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 设置文本缓存 1 MB
        container.setMaxTextMessageBufferSize(1024 * 1024);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketEditorHandler(), "/ws").addInterceptors(webSocketEditorInterceptor());
    }

    @Bean
    public WebSocketEditorInterceptor webSocketEditorInterceptor() {
        return new WebSocketEditorInterceptor();
    }

    @Bean
    public WebSocketEditorHandler webSocketEditorHandler() {
        return new WebSocketEditorHandler();
    }
}
