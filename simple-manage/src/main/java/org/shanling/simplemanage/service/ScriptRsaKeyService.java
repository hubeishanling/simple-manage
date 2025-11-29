package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.ScriptRsaKeyDTO;
import org.shanling.simplemanage.entity.ScriptRsaKey;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptRsaKeyMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚本RSA密钥管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptRsaKeyService {

    private final ScriptRsaKeyMapper rsaKeyMapper;
    private final ScriptConfigService configService;

    public ScriptRsaKeyService(ScriptRsaKeyMapper rsaKeyMapper, ScriptConfigService configService) {
        this.rsaKeyMapper = rsaKeyMapper;
        this.configService = configService;
    }

    /**
     * 分页查询RSA密钥列表
     */
    public IPage<ScriptRsaKey> getRsaKeyList(long current, long size, String keyName, Integer status) {
        log.info("查询RSA密钥列表：current={}, size={}, keyName={}, status={}", current, size, keyName, status);
        
        Page<ScriptRsaKey> page = new Page<>(current, size);
        LambdaQueryWrapper<ScriptRsaKey> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(keyName)) {
            wrapper.like(ScriptRsaKey::getKeyName, keyName);
        }
        if (status != null) {
            wrapper.eq(ScriptRsaKey::getStatus, status);
        }
        
        wrapper.orderByDesc(ScriptRsaKey::getIsDefault);
        wrapper.orderByDesc(ScriptRsaKey::getCreateTime);
        
        return rsaKeyMapper.selectPage(page, wrapper);
    }

    /**
     * 获取RSA密钥详情
     */
    public ScriptRsaKey getRsaKeyDetail(String id) {
        log.info("查询RSA密钥详情：id={}", id);
        ScriptRsaKey rsaKey = rsaKeyMapper.selectById(id);
        if (rsaKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "RSA密钥不存在");
        }
        return rsaKey;
    }

    /**
     * 添加RSA密钥
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRsaKey(ScriptRsaKeyDTO rsaKeyDTO) {
        log.info("添加RSA密钥：{}", rsaKeyDTO.getKeyName());
        
        ScriptRsaKey rsaKey = new ScriptRsaKey();
        BeanUtils.copyProperties(rsaKeyDTO, rsaKey);
        
        // 如果设置为默认密钥，需要将其他默认密钥取消
        if (rsaKey.getIsDefault() != null && rsaKey.getIsDefault() == 1) {
            clearDefaultKey(null);
        }
        
        rsaKey.setCreateTime(LocalDateTime.now());
        rsaKey.setUpdateTime(LocalDateTime.now());
        
        rsaKeyMapper.insert(rsaKey);
        log.info("RSA密钥添加成功：{}", rsaKey.getKeyName());
    }

    /**
     * 更新RSA密钥
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRsaKey(ScriptRsaKeyDTO rsaKeyDTO) {
        log.info("更新RSA密钥：id={}", rsaKeyDTO.getId());
        
        ScriptRsaKey rsaKey = rsaKeyMapper.selectById(rsaKeyDTO.getId());
        if (rsaKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "RSA密钥不存在");
        }
        
        BeanUtils.copyProperties(rsaKeyDTO, rsaKey);
        
        // 如果设置为默认密钥，需要将其他默认密钥取消
        if (rsaKey.getIsDefault() != null && rsaKey.getIsDefault() == 1) {
            clearDefaultKey(rsaKey.getId());
        }
        
        rsaKey.setUpdateTime(LocalDateTime.now());
        
        rsaKeyMapper.updateById(rsaKey);
        log.info("RSA密钥更新成功：{}", rsaKey.getKeyName());
    }

    /**
     * 删除RSA密钥
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRsaKey(String id) {
        log.info("删除RSA密钥：id={}", id);
        
        ScriptRsaKey rsaKey = rsaKeyMapper.selectById(id);
        if (rsaKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "RSA密钥不存在");
        }
        
        // 不允许删除默认密钥
        if (rsaKey.getIsDefault() != null && rsaKey.getIsDefault() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "默认密钥不允许删除");
        }
        
        rsaKeyMapper.deleteById(id);
        log.info("RSA密钥删除成功：{}", rsaKey.getKeyName());
    }

    /**
     * 批量删除RSA密钥
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRsaKey(Collection<String> ids) {
        log.info("批量删除RSA密钥：ids={}", ids);
        
        // 检查是否包含默认密钥
        for (String id : ids) {
            ScriptRsaKey rsaKey = rsaKeyMapper.selectById(id);
            if (rsaKey != null && rsaKey.getIsDefault() != null && rsaKey.getIsDefault() == 1) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "默认密钥不允许删除");
            }
        }
        
        rsaKeyMapper.deleteBatchIds(ids);
        log.info("批量删除RSA密钥成功");
    }

    /**
     * 设置默认密钥
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultKey(String id) {
        log.info("设置默认密钥：id={}", id);
        
        ScriptRsaKey rsaKey = rsaKeyMapper.selectById(id);
        if (rsaKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "RSA密钥不存在");
        }
        
        // 取消其他默认密钥
        clearDefaultKey(id);
        
        // 设置当前为默认
        rsaKey.setIsDefault(1);
        rsaKey.setUpdateTime(LocalDateTime.now());
        rsaKeyMapper.updateById(rsaKey);
        
        log.info("设置默认密钥成功：{}", rsaKey.getKeyName());
    }

    /**
     * 生成RSA密钥对
     */
    public Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            Map<String, String> result = new HashMap<>();
            result.put("publicKey", publicKeyStr);
            result.put("privateKey", privateKeyStr);
            
            log.info("RSA密钥对生成成功");
            return result;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "生成RSA密钥对失败: " + e.getMessage());
        }
    }

    /**
     * 获取默认密钥
     */
    public ScriptRsaKey getDefaultKey() {
        LambdaQueryWrapper<ScriptRsaKey> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptRsaKey::getIsDefault, 1);
        wrapper.eq(ScriptRsaKey::getStatus, 0);
        wrapper.last("LIMIT 1");
        return rsaKeyMapper.selectOne(wrapper);
    }

    /**
     * 生成config.js配置文件
     * 若无默认密钥，则自动创建
     * 使用配置模板生成，支持自定义模板
     */
    public Map<String, String> generateConfigJs(String gameId) {
        log.info("生成config.js配置文件，游戏ID：{}", gameId);
        
        // 获取默认密钥，如不存在则自动创建
        ScriptRsaKey defaultKey = getDefaultKey();
        if (defaultKey == null) {
            log.info("未找到默认密钥，自动创建默认密钥");
            defaultKey = createDefaultKey();
        }
        
        // 使用配置模板服务生成内容
        String content = configService.generateConfigContent(
            "CONFIG_JS",
            gameId,
            defaultKey.getRequestPublicKey(),
            defaultKey.getResponsePrivateKey()
        );
        
        Map<String, String> result = new HashMap<>();
        result.put("content", content);
        result.put("fileName", "config.js");
        
        return result;
    }

    /**
     * 清除默认密钥标记（排除指定ID）
     */
    private void clearDefaultKey(String excludeId) {
        LambdaQueryWrapper<ScriptRsaKey> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptRsaKey::getIsDefault, 1);
        if (StringUtils.hasText(excludeId)) {
            wrapper.ne(ScriptRsaKey::getId, excludeId);
        }
        
        rsaKeyMapper.selectList(wrapper).forEach(key -> {
            key.setIsDefault(0);
            key.setUpdateTime(LocalDateTime.now());
            rsaKeyMapper.updateById(key);
        });
    }

    /**
     * 自动创建默认密钥
     */
    @Transactional(rollbackFor = Exception.class)
    private ScriptRsaKey createDefaultKey() {
        log.info("开始自动创建默认RSA密钥");
        
        try {
            // 生成请求密钥对
            Map<String, String> requestKeyPair = generateKeyPair();
            // 生成响应密钥对
            Map<String, String> responseKeyPair = generateKeyPair();
            
            ScriptRsaKey rsaKey = new ScriptRsaKey();
            rsaKey.setKeyName("默认密钥");
            rsaKey.setRequestPublicKey(requestKeyPair.get("publicKey"));
            rsaKey.setRequestPrivateKey(requestKeyPair.get("privateKey"));
            rsaKey.setResponsePublicKey(responseKeyPair.get("publicKey"));
            rsaKey.setResponsePrivateKey(responseKeyPair.get("privateKey"));
            rsaKey.setStatus(0); // 正常状态
            rsaKey.setIsDefault(1); // 设为默认
            rsaKey.setRemark("系统自动生成的默认密钥");
            rsaKey.setCreateTime(LocalDateTime.now());
            rsaKey.setUpdateTime(LocalDateTime.now());
            
            rsaKeyMapper.insert(rsaKey);
            
            log.info("默认RSA密钥创建成功：ID={}, KeyName={}", rsaKey.getId(), rsaKey.getKeyName());
            return rsaKey;
        } catch (Exception e) {
            log.error("自动创建默认密钥失败", e);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "自动创建默认密钥失败: " + e.getMessage());
        }
    }

}
