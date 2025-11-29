-- 脚本卡密游戏关联表
CREATE TABLE IF NOT EXISTS `script_card_game` (
    `id` VARCHAR(20) NOT NULL COMMENT '主键ID（雪花算法生成）',
    `card_id` VARCHAR(20) NOT NULL COMMENT '卡密ID',
    `game_id` VARCHAR(20) NOT NULL COMMENT '游戏ID',
    `game_title` VARCHAR(100) DEFAULT NULL COMMENT '游戏名称（冗余字段）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_card_id` (`card_id`),
    KEY `idx_game_id` (`game_id`),
    UNIQUE KEY `uk_card_game` (`card_id`, `game_id`)
) COMMENT='脚本卡密游戏关联表';
