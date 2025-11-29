-- 脚本RSA密钥表
CREATE TABLE IF NOT EXISTS `script_rsa_key` (
    `id` VARCHAR(20) NOT NULL COMMENT '主键ID（雪花算法生成）',
    `key_name` VARCHAR(100) NOT NULL COMMENT '密钥名称',
    `request_public_key` TEXT NOT NULL COMMENT '请求加密公钥（前端使用）',
    `request_private_key` TEXT NOT NULL COMMENT '请求解密私钥（后端使用）',
    `response_public_key` TEXT NOT NULL COMMENT '响应加密公钥（后端使用）',
    `response_private_key` TEXT NOT NULL COMMENT '响应解密私钥（前端使用）',
    `status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `is_default` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否为默认密钥（0否 1是）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_key_name` (`key_name`),
    KEY `idx_status` (`status`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_create_time` (`create_time`)
) COMMENT='脚本RSA密钥表';
