package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.config.UserConfig;
import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.exception.BadRequestException;
import com.github.nekolr.slime.security.AuthenticationInfo;
import com.github.nekolr.slime.service.UserService;
import com.github.nekolr.slime.util.IdGenerator;
import com.github.nekolr.slime.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;

@Component
public class UserServiceImpl implements UserService {

    @Resource
    private UserConfig userConfig;

    @Value("${jwt.period}")
    private Duration period;

    @Override
    public User findByUsername(String username) {
        if (userConfig.getUsername().equals(username)) {
            return new User(userConfig.getUsername(), userConfig.getPassword());
        }
        return null;
    }

    @Override
    public AuthenticationInfo login(User user) {
        User entity = findByUsername(user.getUsername());
        if (Objects.isNull(entity) || !user.getPassword().equals(entity.getPassword())) {
            throw new BadRequestException("无效的用户名或密码");
        }
        // 校验完成后签发 Token
        String token = JwtUtils.issueJwt(IdGenerator.randomUUID(), user.getUsername(),
                "", period.getSeconds(), "");

        return new AuthenticationInfo(token, user);
    }
}
