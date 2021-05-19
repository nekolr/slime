package com.github.nekolr.slime.service;

import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.security.AuthenticationInfo;

public interface UserService {

    User findByUsername(String username);

    AuthenticationInfo login(User user);
}
