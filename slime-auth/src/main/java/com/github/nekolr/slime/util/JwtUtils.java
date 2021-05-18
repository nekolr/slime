package com.github.nekolr.slime.util;

import com.github.nekolr.slime.security.JwtUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类
 */
public class JwtUtils {

    /**
     * 私钥
     */
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * 签发令牌
     *
     * @param jwtId   令牌 ID
     * @param subject 被签发者
     * @param issuer  签发者
     * @param period  有效时间，单位秒
     * @return
     */
    public static String issueJwt(String jwtId, String subject, String issuer,
                                  Long period, String audience) {
        // 当前时间戳
        Long currentTimeMillis = System.currentTimeMillis();
        // 创建 JwtBuilder
        JwtBuilder jwtBuilder = Jwts.builder();

        /* Reserved claims */

        // 设置令牌 ID
        jwtBuilder.setId(jwtId);
        // 设置被签发者
        jwtBuilder.setSubject(subject);
        // 设置签发者
        jwtBuilder.setIssuer(issuer);
        // 设置接收者
        jwtBuilder.setAudience(audience);
        // 设置签发时间
        jwtBuilder.setIssuedAt(new Date(currentTimeMillis));
        // 设置到期时间
        jwtBuilder.setExpiration(new Date(currentTimeMillis + period * 1000));

        /* Private claims */

        // 目前为空，可以根据需要添加


        // 压缩方式，默认采用 deflate
        jwtBuilder.compressWith(CompressionCodecs.DEFLATE);
        // 签发令牌
        jwtBuilder.signWith(SECRET_KEY, SignatureAlgorithm.HS512);
        // 生成令牌
        return jwtBuilder.compact();
    }

    /**
     * 解析 JWT
     *
     * @param jwt 令牌
     * @return
     */
    public static JwtUser parseJwt(String jwt) {
        // 获取有效荷载
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(jwt).getBody();

        JwtUser jwtUser = new JwtUser();
        jwtUser.setJwtId(claims.getId());
        jwtUser.setUsername(claims.getSubject());
        jwtUser.setIssuer(claims.getIssuer());
        jwtUser.setIssuedAt(claims.getIssuedAt());
        jwtUser.setAudience(claims.getAudience());
        return jwtUser;
    }

}
