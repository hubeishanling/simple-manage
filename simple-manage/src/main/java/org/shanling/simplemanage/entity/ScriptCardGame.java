package org.shanling.simplemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 脚本卡密游戏关联实体类
 * 
 * @author shanling
 */
@Data
@TableName("script_card_game")
public class ScriptCardGame {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 卡密ID
     */
    private String cardId;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 游戏名称（冗余字段）
     */
    private String gameTitle;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
