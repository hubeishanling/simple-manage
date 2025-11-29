package org.shanling.simplemanage.controller;

import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello Controller - Example REST Controller
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public Result<Map<String, Object>> hello() {
        log.info("访问 hello 接口");
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome to Simple Manage System!");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0");
        
        log.debug("返回数据：{}", data);
        return Result.success(data);
    }

    /**
     * 测试异常处理
     */
    @GetMapping("/error")
    public Result<Void> testError(@RequestParam(required = false) String type) {
        log.warn("测试异常，类型：{}", type);
        
        if ("business".equals(type)) {
            throw new BusinessException("这是一个业务异常");
        } else if ("null".equals(type)) {
            throw new NullPointerException("这是一个空指针异常");
        } else {
            throw new RuntimeException("这是一个运行时异常");
        }
    }

}
