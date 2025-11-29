package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.UserDTO;
import org.shanling.simplemanage.entity.User;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询用户列表
     */
    public IPage<User> getUserList(long current, long size, String username) {
        log.info("查询用户列表：current={}, size={}, username={}", current, size, username);
        
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 获取用户详情
     */
    public User getUserDetail(String id) {
        log.info("查询用户详情：id={}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 添加用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO userDTO) {
        log.info("添加用户：{}", userDTO.getUsername());
        
        // 检查用户名是否已存在
        User existUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUsername()));
        
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 密码不能为空
        if (!StringUtils.hasText(userDTO.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "密码不能为空");
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        log.info("用户添加成功：{}", user.getUsername());
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        log.info("更新用户：id={}", userDTO.getId());
        
        User user = userMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 更新字段
        if (StringUtils.hasText(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (StringUtils.hasText(userDTO.getRemark())) {
            user.setRemark(userDTO.getRemark());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        log.info("用户更新成功：{}", user.getUsername());
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        log.info("删除用户：id={}", id);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // admin 账号不可删除
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "admin 账号不可删除");
        }
        
        userMapper.deleteById(id);
        log.info("用户删除成功：{}", user.getUsername());
    }

}
