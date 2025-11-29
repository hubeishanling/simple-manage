package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.config.JwtProperties;
import org.shanling.simplemanage.dto.LoginRequest;
import org.shanling.simplemanage.dto.LoginResponse;
import org.shanling.simplemanage.dto.UserInfo;
import org.shanling.simplemanage.entity.User;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.UserMapper;
import org.shanling.simplemanage.util.IpUtil;
import org.shanling.simplemanage.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil, JwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 用户登录
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("用户登录：{}", request.getUsername());

        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null) {
            log.warn("用户不存在：{}", request.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            log.warn("账号已被禁用：{}", request.getUsername());
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("密码错误：{}", request.getUsername());
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 更新登录信息
        String ip = IpUtil.getIpAddress(httpRequest);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userMapper.updateById(user);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        log.info("用户登录成功：{}，IP：{}", request.getUsername(), ip);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token,
                jwtProperties.getExpiration()
        );
    }

    /**
     * 获取当前用户信息
     */
    public UserInfo getCurrentUser(String username) {
        log.debug("获取用户信息：{}", username);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }

}
