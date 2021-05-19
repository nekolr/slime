package com.github.nekolr.slime.util;

import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.exception.BadRequestException;

public class SecurityContextHolder {

    public static User getUser() {
        User user;
        try {
            user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BadRequestException("未授权");
        }
        return user;
    }
}
