package org.shanling.simplemanage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * 
 * @author shanling
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret = "simple-manage-secret-key-2024-must-be-at-least-256-bits-long-for-hs256";

    /**
     * Token 有效期（毫秒），默认 7 天
     */
    private Long expiration = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Token 前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Token 请求头名称
     */
    private String header = "Authorization";

}
