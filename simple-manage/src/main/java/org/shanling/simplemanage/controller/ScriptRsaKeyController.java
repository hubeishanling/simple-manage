package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.ScriptRsaKeyDTO;
import org.shanling.simplemanage.entity.ScriptRsaKey;
import org.shanling.simplemanage.service.ScriptRsaKeyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 脚本RSA密钥管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/script/rsaKey")
public class ScriptRsaKeyController {

    private final ScriptRsaKeyService rsaKeyService;

    public ScriptRsaKeyController(ScriptRsaKeyService rsaKeyService) {
        this.rsaKeyService = rsaKeyService;
    }

    /**
     * 获取RSA密钥列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<ScriptRsaKey>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyName,
            @RequestParam(required = false) Integer status) {
        log.info("查询RSA密钥列表：current={}, size={}, keyName={}, status={}", current, size, keyName, status);
        IPage<ScriptRsaKey> page = rsaKeyService.getRsaKeyList(current, size, keyName, status);
        return Result.success(page);
    }

    /**
     * 获取RSA密钥详情
     */
    @GetMapping("/detail/{id}")
    public Result<ScriptRsaKey> detail(@PathVariable String id) {
        log.info("查询RSA密钥详情：id={}", id);
        ScriptRsaKey rsaKey = rsaKeyService.getRsaKeyDetail(id);
        return Result.success(rsaKey);
    }

    /**
     * 添加RSA密钥
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody ScriptRsaKeyDTO rsaKeyDTO) {
        log.info("添加RSA密钥：{}", rsaKeyDTO.getKeyName());
        rsaKeyService.addRsaKey(rsaKeyDTO);
        return Result.success("添加成功", null);
    }

    /**
     * 更新RSA密钥
     */
    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody ScriptRsaKeyDTO rsaKeyDTO) {
        log.info("更新RSA密钥：id={}", rsaKeyDTO.getId());
        rsaKeyService.updateRsaKey(rsaKeyDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除RSA密钥
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除RSA密钥：id={}", id);
        rsaKeyService.deleteRsaKey(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除RSA密钥
     */
    @DeleteMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<String> ids) {
        log.info("批量删除RSA密钥：ids={}", ids);
        rsaKeyService.batchDeleteRsaKey(ids);
        return Result.success("批量删除成功", null);
    }

    /**
     * 设置默认密钥
     */
    @PutMapping("/setDefault/{id}")
    public Result<String> setDefault(@PathVariable String id) {
        log.info("设置默认密钥：id={}", id);
        rsaKeyService.setDefaultKey(id);
        return Result.success("设置成功", null);
    }

    /**
     * 生成RSA密钥对
     */
    @GetMapping("/generateKeyPair")
    public Result<Map<String, String>> generateKeyPair() {
        log.info("生成RSA密钥对");
        Map<String, String> keyPair = rsaKeyService.generateKeyPair();
        return Result.success(keyPair);
    }

    /**
     * 生成config.js配置文件
     */
    @GetMapping("/generateConfig")
    public Result<Map<String, String>> generateConfig(@RequestParam(required = false) String gameId) {
        log.info("生成config.js配置文件，游戏ID：{}", gameId);
        Map<String, String> config = rsaKeyService.generateConfigJs(gameId);
        return Result.success(config);
    }

}
