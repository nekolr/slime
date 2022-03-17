package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.entity.User;
import com.github.nekolr.slime.security.LoginVo;
import com.github.nekolr.slime.service.UserService;
import com.github.nekolr.slime.util.MySecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 身份凭证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginVo> login(@Validated User user) {
        return ResponseEntity.ok(userService.login(user));
    }

    @GetMapping("/info")
    public ResponseEntity<User> userinfo() {
        User user = MySecurityContextHolder.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
