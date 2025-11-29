package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.UserDTO;
import org.shanling.simplemanage.entity.User;
import org.shanling.simplemanage.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String username) {
        log.info("查询用户列表：current={}, size={}, username={}", current, size, username);
        IPage<User> page = userService.getUserList(current, size, username);
        return Result.success(page);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/detail/{id}")
    public Result<User> detail(@PathVariable String id) {
        log.info("查询用户详情：id={}", id);
        User user = userService.getUserDetail(id);
        return Result.success(user);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody UserDTO userDTO) {
        log.info("添加用户：{}", userDTO.getUsername());
        userService.addUser(userDTO);
        return Result.success("添加成功", null);
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody UserDTO userDTO) {
        log.info("更新用户：id={}", userDTO.getId());
        userService.updateUser(userDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除用户：id={}", id);
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

}
