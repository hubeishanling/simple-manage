package org.shanling.simplemanage.service;

import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.entity.ScriptConfig;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 脚本配置模板管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptConfigService {

    private final ScriptConfigMapper configMapper;

    public ScriptConfigService(ScriptConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    /**
     * 根据配置类型获取默认配置模板
     */
    public ScriptConfig getDefaultConfig(String configType) {
        log.info("获取默认配置模板：configType={}", configType);
        
        if (!StringUtils.hasText(configType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "配置类型不能为空");
        }
        
        ScriptConfig config = configMapper.selectDefaultByType(configType);
        if (config == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "未找到配置类型为 " + configType + " 的默认模板");
        }
        
        return config;
    }

    /**
     * 生成配置内容（替换占位符）
     */
    public String generateConfigContent(String configType, String gameId, String publicKey, String privateKey) {
        log.info("生成配置内容：configType={}, gameId={}", configType, gameId);
        
        // 获取默认模板
        ScriptConfig config = getDefaultConfig(configType);
        String content = config.getConfigContent();
        
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "配置模板内容为空");
        }
        
        // 替换占位符
        content = content
                .replace("${GAME_ID}", gameId != null ? gameId : "YOUR_GAME_ID_HERE")
                .replace("${PUBLIC_KEY}", publicKey != null ? publicKey : "")
                .replace("${PRIVATE_KEY}", privateKey != null ? privateKey : "")
                .replace("${GENERATE_TIME}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return content;
    }

}
