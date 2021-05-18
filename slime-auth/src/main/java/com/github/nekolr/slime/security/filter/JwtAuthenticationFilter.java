package com.github.nekolr.slime.security.filter;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.security.JwtUser;
import com.github.nekolr.slime.service.UserService;
import com.github.nekolr.slime.util.JwtUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 自定义访问权限校验过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(Constants.TOKEN_HEADER_KEY);

        if (StringUtils.isBlank(header) || !header.startsWith(Constants.TOKEN_HEADER_VALUE_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = StringUtils.replace(header, Constants.TOKEN_HEADER_VALUE_PREFIX, "");
        try {
            // 只判断 token 合法有效，真正的用户信息通过查询得到
            JwtUser jwtUser = JwtUtils.parseJwt(jwt);
            // 只有在 Authentication 为空时才会放入
            if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                User user = userService.findByUsername(jwtUser.getUsername());
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, null);

                log.debug("Authorized user '{}', setting security context", jwtUser.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException e) {
            // token 过期
            log.warn("Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // token 格式错误
            log.warn("Token format error: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // token 构造错误
            log.warn("Token construct error: {}", e.getMessage());
        } catch (SignatureException e) {
            // 签名失败
            log.warn("Signature failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // 非法参数
            log.warn("Illegal argument: {}", e.getMessage());
        } catch (JwtException e) {
            // 其他异常
            log.warn("Other exception: {}", e.getMessage());
        }
        chain.doFilter(request, response);
    }
}
