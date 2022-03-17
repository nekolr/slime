package com.github.nekolr.slime.util;

import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.exception.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;

public class MySecurityContextHolder {

    public static User getCurrentUser() {
        User user;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BadRequestException("未授权");
        }
        return user;
    }
}
