package org.shanling.simplemanage.config.encrypt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.entity.ScriptRsaKey;
import org.shanling.simplemanage.mapper.ScriptRsaKeyMapper;
import org.shanling.simplemanage.util.EncryptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 脚本RSA密钥提供器
 * 从数据库中获取加密密钥，如果没有则自动创建
 *
 * @author shanling
 */
@Slf4j
@Component
public class ScriptRsaKeyProvider {

    private final ScriptRsaKeyMapper rsaKeyMapper;

    public ScriptRsaKeyProvider(ScriptRsaKeyMapper rsaKeyMapper) {
        this.rsaKeyMapper = rsaKeyMapper;
    }

    /**
     * 获取默认密钥（状态正常且标记为默认）
     * 如果不存在则自动创建
     *
     * @return 默认密钥
     */
    public ScriptRsaKey getDefaultKey() {
        LambdaQueryWrapper<ScriptRsaKey> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptRsaKey::getIsDefault, 1);
        wrapper.eq(ScriptRsaKey::getStatus, 0);
        wrapper.last("LIMIT 1");
        
        ScriptRsaKey defaultKey = rsaKeyMapper.selectOne(wrapper);
        
        // 如果没有默认密钥，自动创建
        if (defaultKey == null) {
            log.warn("未找到默认RSA密钥，正在自动创建...");
            defaultKey = createDefaultKey();
        }
        
        return defaultKey;
    }

    /**
     * 获取请求解密私钥
     *
     * @return 请求解密私钥，如果未找到返回null
     */
    public String getRequestPrivateKey() {
        ScriptRsaKey defaultKey = getDefaultKey();
        if (defaultKey != null) {
            return defaultKey.getRequestPrivateKey();
        }
        return null;
    }

    /**
     * 获取响应加密公钥
     *
     * @return 响应加密公钥，如果未找到返回null
     */
    public String getResponsePublicKey() {
        ScriptRsaKey defaultKey = getDefaultKey();
        if (defaultKey != null) {
            return defaultKey.getResponsePublicKey();
        }
        return null;
    }
    
    /**
     * 自动创建默认密钥
     */
    @Transactional(rollbackFor = Exception.class)
    private ScriptRsaKey createDefaultKey() {
        log.info("开始自动创建默认RSA密钥");
        
        try {
            // 生成请求密钥对
            Map<String, String> requestKeyPair = EncryptUtils.generateRsaKey();
            // 生成响应密钥对
            Map<String, String> responseKeyPair = EncryptUtils.generateRsaKey();
            
            ScriptRsaKey rsaKey = new ScriptRsaKey();
            rsaKey.setKeyName("默认密钥");
            rsaKey.setRequestPublicKey(requestKeyPair.get("publicKey"));
            rsaKey.setRequestPrivateKey(requestKeyPair.get("privateKey"));
            rsaKey.setResponsePublicKey(responseKeyPair.get("publicKey"));
            rsaKey.setResponsePrivateKey(responseKeyPair.get("privateKey"));
            rsaKey.setStatus(0); // 正常状态
            rsaKey.setIsDefault(1); // 设为默认
            rsaKey.setRemark("系统自动生成的默认密钥（用于API加密）");
            rsaKey.setCreateTime(new Date());
            rsaKey.setUpdateTime(new Date());
            
            rsaKeyMapper.insert(rsaKey);
            
            log.info("默认RSA密钥创建成功：ID={}, KeyName={}", rsaKey.getId(), rsaKey.getKeyName());
            log.info("请求公钥（前端配置）：{}", rsaKey.getRequestPublicKey());
            log.info("响应私钥（前端配置）：{}", rsaKey.getResponsePrivateKey());
            
            return rsaKey;
        } catch (Exception e) {
            log.error("自动创建默认密钥失败", e);
            throw new RuntimeException("自动创建默认密钥失败: " + e.getMessage());
        }
    }
}
