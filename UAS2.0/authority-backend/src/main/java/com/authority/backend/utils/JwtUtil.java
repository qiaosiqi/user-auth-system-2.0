package com.authority.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    @Value("${security.jwt.secret:}")
    private String secret;

    @Value("${security.jwt.expiration-ms:604800000}")
    private long expirationTime;

    @PostConstruct
    void validateConfiguration() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must contain at least 32 characters");
        }
    }

    /**
     * 1. 根据用户信息生成 Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // 将用户ID放入 Payload
        claims.put("username", username); // 将用户名放入 Payload

        return Jwts.builder()
                .setClaims(claims)           // 设置自定义 Payload
                .setIssuedAt(now)            // 签发时间
                .setExpiration(expirationDate) // 过期时间
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 2. 从 Token 中解析出 Payload (Claims)
     * @param token JWT Token 字符串
     * @return Claims 对象 (包含 userId, username 等)
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 解析失败（Token过期、签名错误等）
            return null;
        }
    }

    /**
     * 3. 校验 Token 是否有效
     * @param token JWT Token 字符串
     * @return boolean
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }
}
