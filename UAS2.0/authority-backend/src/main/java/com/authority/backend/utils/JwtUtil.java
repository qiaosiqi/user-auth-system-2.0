package com.authority.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
public class JwtUtil {

    // 令牌秘钥（随便设置，但必须足够复杂且保密）
    private static final String SECRET = "your_complex_secret_key_for_authority_system";

    // 令牌的有效时间（这里设置为 7 天）
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 毫秒

    /**
     * 1. 根据用户信息生成 Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // 将用户ID放入 Payload
        claims.put("username", username); // 将用户名放入 Payload

        return Jwts.builder()
                .setClaims(claims)           // 设置自定义 Payload
                .setIssuedAt(now)            // 签发时间
                .setExpiration(expirationDate) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET) // 签名算法和秘钥
                .compact();
    }

    /**
     * 2. 从 Token 中解析出 Payload (Claims)
     * @param token JWT Token 字符串
     * @return Claims 对象 (包含 userId, username 等)
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET) // 使用相同的秘钥解析
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
    public static boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }
}