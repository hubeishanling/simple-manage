package org.shanling.simplemanage.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.config.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 
 * @author shanling
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 生成密钥
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return Token
     */
    public String generateToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims);
    }

    /**
     * 创建 Token
     *
     * @param claims 声明信息
     * @return Token
     */
    private String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     *
     * @param token Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 Token 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token Token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", String.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("验证 Token 失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 从请求头中获取 Token
     *
     * @param authHeader 请求头
     * @return Token
     */
    public String getTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            return authHeader.substring(jwtProperties.getTokenPrefix().length());
        }
        return null;
    }

}
