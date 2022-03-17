package com.github.nekolr.slime.security;

import com.github.nekolr.slime.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 签发令牌后返回给前端的实体
 */
@Getter
@AllArgsConstructor
public class LoginVo implements Serializable {

    private String token;

    private User user;
}
