package org.shanling.simplemanage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.LoginRequest;
import org.shanling.simplemanage.dto.LoginResponse;
import org.shanling.simplemanage.dto.UserInfo;
import org.shanling.simplemanage.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("登录请求：{}", request.getUsername());
        LoginResponse response = authService.login(request, httpRequest);
        return Result.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<UserInfo> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("获取当前用户信息：{}", username);
        UserInfo userInfo = authService.getCurrentUser(username);
        return Result.success(userInfo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("用户退出登录：{}", authentication.getName());
        }
        SecurityContextHolder.clearContext();
        return Result.success("退出成功", null);
    }

}
