package com.github.nekolr.slime.config;

import com.github.nekolr.slime.security.filter.JwtAuthenticationEntryPoint;
import com.github.nekolr.slime.security.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                // 关闭登出
                .logout().disable()
                // 关闭 csrf
                .csrf().disable()

                // X-Frame-Options: SAMEORIGIN
                .headers()
                .frameOptions()
                .sameOrigin()

                // X-Content-Type-Options: nosniff
                .and()
                .headers()
                .contentTypeOptions()

                // X-XSS-Protection: 1; mode=block
                .and().and()
                .headers()
                .xssProtection()
                .xssProtectionEnabled(true)

                // 授权异常处理
                .and().and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)

                // 不需要 session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 过滤请求
                .and()
                .authorizeRequests()
                // OPTIONS 预检请求可以匿名访问
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()
                // 静态资源可以匿名访问
                .antMatchers(HttpMethod.GET, "/js/**").anonymous()
                .antMatchers(HttpMethod.GET, "/css/**").anonymous()
                .antMatchers(HttpMethod.GET, "/images/**").anonymous()
                .antMatchers(HttpMethod.GET, "/*.html").anonymous()
                // 主页可以匿名访问
                .antMatchers(HttpMethod.GET, "/").anonymous()
                // 登录请求不拦截（如果登录请求头包含 Authorization: Bearer 任意字符，那么还是会进行校验）
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                // 允许 websocket 请求
                .antMatchers("/ws").permitAll()

                // 其他所有请求都要经过验证
                .anyRequest().authenticated();

        httpSecurity
                // 添加登录和权限校验的两个过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
