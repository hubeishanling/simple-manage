-- 脚本版本控制表
CREATE TABLE IF NOT EXISTS `script_version_control` (
    `id` VARCHAR(20) NOT NULL COMMENT '版本ID（雪花算法生成）',
    `game_id` VARCHAR(20) NOT NULL COMMENT '游戏ID',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件地址',
    `type` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '文件类型(0:APK插件)',
    `version` INT(11) NOT NULL COMMENT '版本(每次上传代码版本自动+1)',
    `file_md5` VARCHAR(32) DEFAULT NULL COMMENT '文件MD5值',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_game_id` (`game_id`),
    KEY `idx_version` (`version`),
    KEY `idx_create_time` (`create_time`)
) COMMENT='脚本版本控制表';
