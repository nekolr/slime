package com.github.nekolr.slime.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 当用户尝试访问安全的 REST 资源而不提供任何凭证时，将调用此方法发送 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException == null ? "未授权" : authException.getMessage());
    }
}
