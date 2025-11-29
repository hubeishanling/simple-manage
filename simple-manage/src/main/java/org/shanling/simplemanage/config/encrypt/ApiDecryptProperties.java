package org.shanling.simplemanage.config.encrypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * API加密配置属性
 *
 * @author shanling
 */
@Data
@Component
@ConfigurationProperties(prefix = "api-decrypt")
public class ApiDecryptProperties {

    /**
     * 加密开关
     */
    private Boolean enabled = false;

    /**
     * 头部标识
     */
    private String headerFlag = "encrypt-key";

    /**
     * 默认响应加密公钥（如果数据库中没有配置密钥，使用此默认值）
     */
    private String publicKey;

    /**
     * 默认请求解密私钥（如果数据库中没有配置密钥，使用此默认值）
     */
    private String privateKey;

}
