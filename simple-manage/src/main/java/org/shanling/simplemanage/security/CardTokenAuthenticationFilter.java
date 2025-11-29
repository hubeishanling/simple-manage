package org.shanling.simplemanage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 卡密Token认证过滤器
 * 专门用于处理 /open-api/** 接口的卡密token认证
 * 
 * @author shanling
 */
@Slf4j
@Component
public class CardTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public CardTokenAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 只处理 /open-api/script/** 路径（排除login和checkUser）
        if (requestUri.startsWith("/open-api/script/") 
                && !requestUri.equals("/open-api/script/login")
                && !requestUri.equals("/open-api/script/checkUser")) {

            try {
                // 获取token（支持两种格式）
                String cardToken = getCardToken(request);

                if (cardToken != null && jwtUtil.validateToken(cardToken)) {
                    // 从token中获取用户名（格式：card:卡号）
                    String username = jwtUtil.getUsernameFromToken(cardToken);

                    if (username != null && username.startsWith("card:")) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CARD_USER"))
                        );

                        // 设置到Security上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("卡密token验证通过：{}", username);
                    }
                }
            } catch (Exception e) {
                log.warn("卡密token验证失败：{}", e.getMessage());
                // 不阻止请求继续，让Controller层处理
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取卡密token（支持两种格式）
     */
    private String getCardToken(HttpServletRequest request) {
        // 1. 从Authorization头获取（Bearer token格式）
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        // 2. 从Card-Token头获取
        String cardToken = request.getHeader("Card-Token");
        if (cardToken != null) {
            return cardToken;
        }

        return null;
    }

}
