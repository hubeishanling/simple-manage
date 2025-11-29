-- 脚本配置模板表
CREATE TABLE IF NOT EXISTS `script_config` (
    `id` VARCHAR(20) NOT NULL COMMENT '主键ID（雪花算法生成）',
    `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `config_type` VARCHAR(50) NOT NULL COMMENT '配置类型（如：CONFIG_JS, VERSION_JSON等）',
    `config_content` TEXT NOT NULL COMMENT '配置内容（支持占位符：${GAME_ID}, ${PUBLIC_KEY}, ${PRIVATE_KEY}等）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
    `status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `is_default` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否为默认配置（0否 1是）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_config_type` (`config_type`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_status` (`status`)
) COMMENT='脚本配置模板表';

-- 插入默认的 config.js 模板
INSERT INTO `script_config` (`id`, `config_name`, `config_type`, `config_content`, `description`, `status`, `is_default`) VALUES
('1', 'config.js默认模板', 'CONFIG_JS', 
'/**
 * 脚本配置文件
 * 自动生成时间：\${GENERATE_TIME}
 */

const config = {
  // 游戏ID
  GAME_ID: \'\${GAME_ID}\',

  // 服务器配置
  BASE_URL: \'http://your-server.com/api\',
  
  // RSA密钥配置
  RSA: {
    // 请求加密公钥（前端使用）
    REQUEST_PUBLIC_KEY: `\${PUBLIC_KEY}`,

    // 响应解密私钥（前端使用）
    RESPONSE_PRIVATE_KEY: `\${PRIVATE_KEY}`
  }
};

// 导出配置
module.exports = config;',
'config.js文件的默认模板，支持变量替换', 0, 1);
