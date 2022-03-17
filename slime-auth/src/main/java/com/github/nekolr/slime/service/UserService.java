package com.github.nekolr.slime.service;

import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.security.LoginVo;

public interface UserService {

    User findByUsername(String username);

    LoginVo login(User user);
}
