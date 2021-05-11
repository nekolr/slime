package com.github.nekolr.slime.config;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.websocket.WebSocketEditorServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter endpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setSpider(Spider spider) {
        WebSocketEditorServer.spider = spider;
    }
}
