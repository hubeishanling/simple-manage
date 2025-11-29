package org.shanling.simplemanage.config;

import org.shanling.simplemanage.security.CardTokenAuthenticationFilter;
import org.shanling.simplemanage.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 
 * @author shanling
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CardTokenAuthenticationFilter cardTokenAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CardTokenAuthenticationFilter cardTokenAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.cardTokenAuthenticationFilter = cardTokenAuthenticationFilter;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 允许访问管理端登录接口
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        // 允许访问测试接口
                        .requestMatchers("/hello/**").permitAll()
                        // 脚本端其他接口需要卡密认证（ROLE_CARD_USER）
                        .requestMatchers("/open-api/script/**").permitAll()
                        // 允许访问文件下载
                        .requestMatchers("/files/**").permitAll()
                        // 其他请求需要管理员认证
                        .anyRequest().authenticated()
                )
                
                // 配置会话管理（无状态）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 添加卡密Token过滤器（优先级高，先验证卡密token）
                .addFilterBefore(cardTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加管理员JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
