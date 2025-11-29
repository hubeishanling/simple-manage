-- 游戏列表表
CREATE TABLE IF NOT EXISTS `script_game` (
    `id` VARCHAR(20) NOT NULL COMMENT '游戏ID（雪花算法生成）',
    `title` VARCHAR(100) NOT NULL COMMENT '游戏名称',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_title` (`title`),
    KEY `idx_create_time` (`create_time`)
) COMMENT='游戏列表表';
