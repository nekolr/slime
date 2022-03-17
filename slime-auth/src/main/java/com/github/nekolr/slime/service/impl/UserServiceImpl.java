package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.config.UserConfig;
import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.exception.BadRequestException;
import com.github.nekolr.slime.security.LoginVo;
import com.github.nekolr.slime.security.filter.TokenProvider;
import com.github.nekolr.slime.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserConfig userConfig;
    private final TokenProvider tokenProvider;

    @Override
    public User findByUsername(String username) {
        if (userConfig.getUsername().equals(username)) {
            return new User(userConfig.getUsername(), userConfig.getPassword());
        }
        return null;
    }

    @Override
    public LoginVo login(User user) {
        User entity = findByUsername(user.getUsername());
        if (Objects.isNull(entity) || !user.getPassword().equals(entity.getPassword())) {
            throw new BadRequestException("无效的用户名或密码");
        }
        String token = tokenProvider.createToken(user.getUsername());
        return new LoginVo(token, user);
    }
}
