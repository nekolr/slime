package com.github.nekolr.slime.security;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * JWT 账户，从令牌中解析出有效荷载后存入该实体
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JwtUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 令牌 ID
     */
    private String jwtId;

    /**
     * 用户唯一标识
     */
    private String username;

    /**
     * 签发者
     */
    private String issuer;

    /**
     * 接收者
     */
    private String audience;

    /**
     * 签发时间
     */
    private Date issuedAt;
}
