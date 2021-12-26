package com.github.nekolr.slime.websocket;

import com.github.nekolr.slime.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketEditorInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_PARAMETER = "token";

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse resp, WebSocketHandler handler, Map<String, Object> attributes) throws Exception {

        if (req instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) req;
            String token = serverRequest.getServletRequest().getParameter(TOKEN_PARAMETER);
            if (StringUtils.isBlank(token)) {
                return false;
            }
            try {
                JwtUtils.parseJwt(token);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse resp, WebSocketHandler handler, Exception e) {

    }
}
