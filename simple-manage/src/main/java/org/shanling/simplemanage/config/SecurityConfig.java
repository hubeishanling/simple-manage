package org.shanling.simplemanage.config;

import org.shanling.simplemanage.security.CardTokenAuthenticationFilter;
import org.shanling.simplemanage.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Spring Security 配置 - 双认证系统架构
 * 实现两套完全独立的认证系统：
 * 1. 脚本端卡密认证系统 (/open-api/script/**)
 * 2. 管理端用户认证系统 (其他路径)
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
     * 脚本端卡密认证系统（独立的安全过滤链）
     * 优先级：1（最高优先级）
     * 处理路径：/open-api/script/**
     */
    @Bean
    @Order(1)
    public SecurityFilterChain scriptSecurityFilterChain(HttpSecurity http) throws Exception {
        // 定义脚本端路径匹配器
        RequestMatcher scriptPathMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/open-api/script/**")
        );

        http
                // 仅匹配脚本端路径
                .securityMatcher(scriptPathMatcher)
                
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置脚本端的授权规则
                .authorizeHttpRequests(auth -> auth
                        // 登录和检查接口无需认证
                        .requestMatchers("/open-api/script/login", "/open-api/script/checkUser").permitAll()
                        // 其他脚本端接口需要卡密认证
                        .anyRequest().authenticated()
                )
                
                // 无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 添加卡密Token过滤器
                .addFilterBefore(cardTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 管理端用户认证系统（独立的安全过滤链）
     * 优先级：2
     * 处理路径：除脚本端外的所有路径
     */
    @Bean
    @Order(2)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置管理端的授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许访问登录接口
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        // 允许访问测试接口
                        .requestMatchers("/hello/**").permitAll()
                        // 允许访问文件下载
                        .requestMatchers("/files/**").permitAll()
                        // 其他管理端接口需要认证
                        .anyRequest().authenticated()
                )
                
                // 无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 添加管理端JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
