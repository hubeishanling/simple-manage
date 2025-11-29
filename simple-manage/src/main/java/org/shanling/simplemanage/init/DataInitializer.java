package org.shanling.simplemanage.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.entity.User;
import org.shanling.simplemanage.mapper.UserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 数据初始化器 - 启动时自动创建管理员账号
 * 
 * @author shanling
 */
@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始初始化数据...");
        initAdminUser();
        log.info("数据初始化完成！");
    }

    /**
     * 初始化管理员账号
     */
    private void initAdminUser() {
        // 检查 admin 账号是否已存在
        User existAdmin = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "admin"));

        if (existAdmin != null) {
            log.info("管理员账号 admin 已存在，跳过初始化");
            return;
        }

        // 创建管理员账号
        User admin = new User();
        admin.setUsername("admin");
        // 密码：admin123，使用 BCrypt 加密
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        admin.setStatus(1);
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        admin.setCreateBy("system");
        admin.setRemark("系统默认管理员账号（username=admin 时不可删除）");

        userMapper.insert(admin);
        log.info("管理员账号 admin 创建成功！默认密码：admin123（注意：admin 账号不可删除）");
    }

}
