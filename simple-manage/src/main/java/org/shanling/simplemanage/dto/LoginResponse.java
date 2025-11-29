package org.shanling.simplemanage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 * 
 * @author shanling
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token 类型
     */
    private String tokenType = "Bearer";

    /**
     * Token 过期时间（毫秒）
     */
    private Long expiresIn;

    public LoginResponse(String userId, String username, String email, String token, Long expiresIn) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = token;
        this.expiresIn = expiresIn;
    }

}
