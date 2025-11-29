-- 脚本卡密表
CREATE TABLE IF NOT EXISTS `script_card` (
    `id` VARCHAR(20) NOT NULL COMMENT '卡密ID（雪花算法生成）',
    `card_no` VARCHAR(50) NOT NULL COMMENT '卡密',
    `expire_day` INT(11) NOT NULL COMMENT '过期天数',
    `price` DECIMAL(10, 2) DEFAULT NULL COMMENT '价格',
    `expire_time` DATETIME DEFAULT NULL COMMENT '实际过期时间（初始为NULL，第一次绑定时赋值）',
    `device_size` INT(11) DEFAULT 1 COMMENT '可绑定设备数',
    `status` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '账号状态（0-停用，1-正常）',
    `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_no` (`card_no`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) COMMENT='脚本卡密表';
